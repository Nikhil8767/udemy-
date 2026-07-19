/**
 * ProgressManager
 * 
 * Single source of truth for handling course progress synchronization 
 * across the entire frontend application without requiring backend hits.
 */

const ProgressManager = {

    /**
     * Helper to get the correct localStorage key
     */
    getKey: function(courseId) {
        return 'course-progress-' + courseId;
    },

    /**
     * Load progress for a specific course from localStorage
     */
    loadProgress: function(courseId) {
        const data = localStorage.getItem(this.getKey(courseId));
        if (data) {
            try {
                return JSON.parse(data);
            } catch (e) {
                console.error("Error parsing progress data", e);
                return null;
            }
        }
        return null;
    },

    /**
     * Save progress to localStorage and dispatch synchronization event
     */
    saveProgress: function(courseId, newProgressData) {
        let existingData = this.loadProgress(courseId) || {};
        
        // Merge the existing data with the new updates
        const mergedData = {
            ...existingData,
            ...newProgressData,
            courseId: courseId,
            updatedAt: new Date().getTime()
        };
        
        // Calculate completion flag if not explicitly set
        if (mergedData.completedLessons !== undefined && mergedData.totalLessons !== undefined) {
            mergedData.progressPercentage = Math.round((mergedData.completedLessons / mergedData.totalLessons) * 100);
            if (mergedData.progressPercentage >= 100) {
                mergedData.progressPercentage = 100;
                mergedData.courseCompleted = true;
            }
        }

        localStorage.setItem(this.getKey(courseId), JSON.stringify(mergedData));
        
        // Dispatch global sync event
        window.dispatchEvent(new CustomEvent("courseProgressUpdated", {
            detail: { courseId: courseId, progress: mergedData }
        }));
        
        return mergedData;
    },

    /**
     * Check if a course is fully completed
     */
    isCourseCompleted: function(courseId) {
        const progress = this.loadProgress(courseId);
        return progress ? (progress.courseCompleted === true || progress.progressPercentage === 100) : false;
    },

    /**
     * Sweeps the DOM for [data-sync="course-card"] elements and patches them
     */
    updateAllCourseCards: function() {
        const cards = document.querySelectorAll('[data-sync="course-card"]');
        cards.forEach(card => {
            const courseId = card.getAttribute('data-course-id');
            if (courseId) {
                this.updateCourseCard(card, courseId);
            }
        });
    },

    /**
     * Patches an individual course card (My Courses / Dashboard Continue Learning)
     */
    updateCourseCard: function(cardElement, courseId) {
        const state = this.loadProgress(courseId);
        if (!state) return;

        const progressText = cardElement.querySelector('.progress-percentage-text');
        const completedLessonsText = cardElement.querySelector('.completed-lessons-text');
        const progressBar = cardElement.querySelector('.progress-bar');
        const continueBtn = cardElement.querySelector('.continue-btn');
        const completionBadge = cardElement.querySelector('.completion-badge');
        
        if (progressText) progressText.innerText = state.progressPercentage;
        if (completedLessonsText) completedLessonsText.innerText = state.completedLessons;
        if (progressBar) {
            progressBar.style.width = state.progressPercentage + '%';
            progressBar.setAttribute('aria-valuenow', state.progressPercentage);
        }

        if (continueBtn) {
            const baseUrl = continueBtn.getAttribute('data-base-url');
            if (state.lastAccessedLessonId && baseUrl) {
                // Determine if we need to append a parameter or hash depending on the app's routing
                const separator = baseUrl.includes('?') ? '&' : '?';
                continueBtn.href = baseUrl + separator + 'lessonId=' + state.lastAccessedLessonId;
            }

            const icon = continueBtn.querySelector('.bi');
            const btnText = continueBtn.querySelector('.continue-text') || continueBtn;

            if (state.courseCompleted || state.progressPercentage === 100) {
                continueBtn.classList.remove('btn-primary', 'btn-outline-primary');
                continueBtn.classList.add('btn-outline-success');
                if (icon) icon.className = 'bi bi-check-circle-fill me-1';
                
                // If there's a span inside the button for text, update it, otherwise update the button text directly
                if (continueBtn.querySelector('.continue-text')) {
                    continueBtn.querySelector('.continue-text').innerText = 'Review Course';
                } else if (!icon) {
                    continueBtn.innerText = 'Review Course';
                }
            } else {
                continueBtn.classList.remove('btn-outline-success');
                // preserve original classes (primary or outline-primary)
                if(!continueBtn.classList.contains('btn-primary') && !continueBtn.classList.contains('btn-outline-primary')){
                     continueBtn.classList.add('btn-primary');
                }
                
                if (icon) icon.className = 'bi bi-play-fill me-1';
                
                if (continueBtn.querySelector('.continue-text')) {
                    continueBtn.querySelector('.continue-text').innerText = 'Continue Learning';
                }
            }
        }

        if (completionBadge) {
            completionBadge.style.display = (state.courseCompleted || state.progressPercentage === 100) ? 'inline-block' : 'none';
        }
    },

    /**
     * Patches the Course Details page elements
     */
    updateCourseDetails: function() {
        const detailsContainer = document.querySelector('[data-sync="course-details"]');
        if (!detailsContainer) return;
        
        const courseId = detailsContainer.getAttribute('data-course-id');
        const state = this.loadProgress(courseId);
        if (!state) return;
        
        const progressText = detailsContainer.querySelector('.details-progress-text');
        const progressBar = detailsContainer.querySelector('.details-progress-bar');
        const actionBtn = detailsContainer.querySelector('.details-action-btn');
        
        if (progressText) progressText.innerText = state.progressPercentage + '% Completed';
        if (progressBar) progressBar.style.width = state.progressPercentage + '%';
        
        if (actionBtn && (state.courseCompleted || state.progressPercentage === 100)) {
            actionBtn.classList.remove('btn-primary');
            actionBtn.classList.add('btn-success');
            actionBtn.innerHTML = '<i class="bi bi-check-circle-fill me-2"></i> Course Completed ✓';
        }
    },
    
    /**
     * Recalculates Dashboard KPIs based on current progress
     */
    updateDashboardKPIs: function() {
        const activeKpi = document.getElementById('kpi-active-courses');
        const completedKpi = document.getElementById('kpi-completed-courses');
        
        if (!activeKpi || !completedKpi) return;
        
        // Count from localStorage how many courses are completed vs active
        let localCompletedCount = 0;
        let localActiveDelta = 0;
        
        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key.startsWith('course-progress-')) {
                try {
                    const state = JSON.parse(localStorage.getItem(key));
                    if (state.courseCompleted || state.progressPercentage === 100) {
                        localCompletedCount++;
                        localActiveDelta++; // For every newly completed course, active goes down by 1
                    }
                } catch(e) {}
            }
        }
        
        // This is a rough estimation for the frontend demo. 
        // We read the original server values, apply the delta, and write back.
        if (!window._originalActiveCourses) {
            window._originalActiveCourses = parseInt(activeKpi.innerText) || 0;
        }
        if (!window._originalCompletedCourses) {
            window._originalCompletedCourses = parseInt(completedKpi.innerText) || 0;
        }
        
        const newCompleted = window._originalCompletedCourses + localCompletedCount;
        const newActive = Math.max(0, window._originalActiveCourses - localActiveDelta);
        
        completedKpi.innerText = newCompleted;
        activeKpi.innerText = newActive;
    },

    init: function() {
        // Run initial synchronization sweeps
        this.updateAllCourseCards();
        this.updateCourseDetails();
        this.updateDashboardKPIs();
        
        // Listen for updates from other tabs or scripts on the same page
        window.addEventListener('courseProgressUpdated', (e) => {
            this.updateAllCourseCards();
            this.updateCourseDetails();
            this.updateDashboardKPIs();
        });
    }
};

// Auto-initialize when the DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    ProgressManager.init();
});
