package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.ApplicantDTO;
import com.jobportal.dto.Application;
import com.jobportal.dto.ApplicationStatus;
import com.jobportal.dto.JobDTO;
import com.jobportal.dto.JobStatus;
import com.jobportal.dto.NotificationDTO;
import com.jobportal.entity.Applicant;
import com.jobportal.entity.Job;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.JobRepository;
import com.jobportal.utility.Utilities; // <-- Import Utilities

@Service("jobService")
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private NotificationService notificationService;

    // 1. INJECT THE UTILITIES BEAN
    @Autowired
    private Utilities utilities;

    @Override
    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
        if(jobDTO.getId()==0) {
            // 2. CALL THE NON-STATIC METHOD
            jobDTO.setId(utilities.getNextSequenceId("jobs"));
            jobDTO.setPostTime(LocalDateTime.now());
            NotificationDTO notiDto=new NotificationDTO();
            notiDto.setAction("Job Posted");
            notiDto.setMessage("Job Posted Successfully for "+jobDTO.getJobTitle()+" at "+ jobDTO.getCompany());
            
            notiDto.setUserId(jobDTO.getPostedBy());
            notiDto.setRoute("/posted-jobs/"+jobDTO.getId());
                notificationService.sendNotification(notiDto);
        }
        else {
            Job job=jobRepository.findById(jobDTO.getId()).orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));
            if(job.getJobStatus().equals(JobStatus.DRAFT) || jobDTO.getJobStatus().equals(JobStatus.CLOSED))jobDTO.setPostTime(LocalDateTime.now());
        }
        return jobRepository.save(jobDTO.toEntity()).toDTO();
    }

    
    @Override
    public List<JobDTO> getAllJobs() throws JobPortalException {
        return jobRepository.findAll().stream().map((x) -> x.toDTO()).toList();
    }

    @Override
    public JobDTO getJob(Long id) throws JobPortalException {
        return jobRepository.findById(id).orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND")).toDTO();
    }

    @Override
    public void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));
        List<Applicant> applicants = job.getApplicants();
        if (applicants == null)applicants = new ArrayList<>();
        // Check if applicantId already exists in the list
        if (applicants.stream().anyMatch(app -> app.getApplicantId() == applicantDTO.getApplicantId())) {
            throw new JobPortalException("JOB_APPLIED_ALREADY");
        }
        applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);
        applicants.add(applicantDTO.toEntity());
        job.setApplicants(applicants);
        jobRepository.save(job);
    }

    @Override
    public List<JobDTO> getHistory(Long id, ApplicationStatus applicationStatus) {
        return jobRepository.findByApplicantIdAndApplicationStatus(id, applicationStatus).stream().map((x) -> x.toDTO())
                .toList();
    }

    @Override
    public List<JobDTO> getJobsPostedBy(Long id) throws JobPortalException {
        return jobRepository.findByPostedBy(id).stream().map((x) -> x.toDTO()).toList();
    }


    /**
     * [FIXED] Refactored this method to be safer.
     * Avoids using .stream().map() for side-effects (like sending notifications).
     * This now iterates, updates, and *then* sends the notification,
     * allowing exceptions to be handled correctly.
     */
    @Override
    public void changeAppStatus(Application application) throws JobPortalException {
        Job job = jobRepository.findById(application.getId()).orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));
        
        List<Applicant> applicants = job.getApplicants();
        boolean interviewScheduled = false; // Flag to send notification later

        if (applicants != null) {
            for (Applicant app : applicants) {
                if (application.getApplicantId() == app.getApplicantId()) {
                    app.setApplicationStatus(application.getApplicationStatus());
                    if (application.getApplicationStatus().equals(ApplicationStatus.INTERVIEWING)) {
                        app.setInterviewTime(application.getInterviewTime());
                        interviewScheduled = true; // Set flag to notify later
                    }
                    break; // Found and updated, no need to continue loop
                }
            }
        }
        
        job.setApplicants(applicants);
        jobRepository.save(job);
        
        // Send notification *after* the database save, and outside the loop
        if (interviewScheduled) {
            NotificationDTO notiDto = new NotificationDTO();
            notiDto.setAction("Interview Scheduled");
            notiDto.setMessage("Interview scheduled for job id: " + application.getId());
            notiDto.setUserId(application.getApplicantId());
            notiDto.setRoute("/job-history");
            
            // This can now properly throw the JobPortalException
            notificationService.sendNotification(notiDto);
        }
    }
}