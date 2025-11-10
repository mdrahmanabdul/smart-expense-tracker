package com.smartexpense.schedulers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.smartexpense.entities.UserEntity;
import com.smartexpense.repositories.UserRepository;
import com.smartexpense.services.EmailService;

@Component
public class ReminderSchedulers {

    private final UserRepository userRepo;
    private final EmailService emailService;
    
    Logger logger = LoggerFactory.getLogger(ReminderSchedulers.class);

    public ReminderSchedulers(UserRepository userRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.emailService = emailService;
    }
    
    

    // Run at 9:00 AM on the 1st day of every month
    @Scheduled(cron = "0 * * * * ?")
    public void sendMonthlyReminders() {
        List<UserEntity> users = userRepo.findAll();
        for (UserEntity user : users) {
            if (user.getEmail() != null) {
                String subject = "Reminder to Upload Monthly Statement - Smart Expense";
                String body = String.format("""
                	    <html>
                	        <body>
                	            <h3>Dear %s,</h3>
                	            <p>This is a **monthly reminder** from SmartExpense Tracker regarding your expense submission for the past month.</p>
                	            <p>We recommend logging in to review, categorize, and submit your financial data (either by uploading a bank statement or manually entering expenses).</p>
                	            
                	            <p>
                	                **Action Required:** Please log in to the application at your earliest convenience.
                	            </p>
                	            
                	            <p>
                	                **Important:** Please disregard this notification if you have already submitted your expenses for the previous reporting period.
                	            </p>
                	            
                	            <br/>
                	            <p>Thank you,</p>
                	            <p>The SmartExpense Tracker Team</p>
                	        </body>
                	    </html>
                	    """, user.getUsername());
                emailService.sendEmail(user.getEmail(), subject, body);
            }
        }
        logger.info("Monthly reminder emails sent successfully");
    }
}