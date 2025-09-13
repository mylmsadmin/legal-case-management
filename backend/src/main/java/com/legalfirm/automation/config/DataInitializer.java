package com.legalfirm.automation.config;

import com.legalfirm.automation.entity.*;
import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.enums.Role;
import com.legalfirm.automation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            ClientRepository clientRepository,
            CaseRepository caseRepository,
            HearingRepository hearingRepository,
            MessageRepository messageRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                return; // Skip initialization if data exists
            }

            // Create Users
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@legalfirm.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .contactNumber("9876543210")
                    .build();
            admin = userRepository.save(admin);

            User lawyer1 = User.builder()
                    .name("John Doe")
                    .email("john.lawyer@legalfirm.com")
                    .password(passwordEncoder.encode("Lawyer@123"))
                    .role(Role.LAWYER)
                    .contactNumber("9876543211")
                    .build();
            lawyer1 = userRepository.save(lawyer1);

            User lawyer2 = User.builder()
                    .name("Jane Smith")
                    .email("jane.lawyer@legalfirm.com")
                    .password(passwordEncoder.encode("Lawyer@123"))
                    .role(Role.LAWYER)
                    .contactNumber("9876543212")
                    .build();
            lawyer2 = userRepository.save(lawyer2);

            User paralegal = User.builder()
                    .name("Sarah Johnson")
                    .email("sarah.para@legalfirm.com")
                    .password(passwordEncoder.encode("Para@123"))
                    .role(Role.PARALEGAL)
                    .contactNumber("9876543213")
                    .build();
            paralegal = userRepository.save(paralegal);

            // Create Clients
            Client client1 = Client.builder()
                    .name("ABC Corporation")
                    .contactInfo("contact@abc.com, 9876543220")
                    .address("123 Business Park, Mumbai, Maharashtra 400001")
                    .build();
            client1 = clientRepository.save(client1);

            Client client2 = Client.builder()
                    .name("XYZ Industries")
                    .contactInfo("info@xyz.com, 9876543221")
                    .address("456 Industrial Area, Delhi, Delhi 110001")
                    .build();
            client2 = clientRepository.save(client2);

            Client client3 = Client.builder()
                    .name("Mr. Raj Kumar")
                    .contactInfo("raj.kumar@email.com, 9876543222")
                    .address("789 Residential Colony, Bangalore, Karnataka 560001")
                    .build();
            client3 = clientRepository.save(client3);

            // Create Cases
            Case case1 = Case.builder()
                    .title("ABC Corporation vs State Tax Department")
                    .description("Tax dispute regarding GST compliance for FY 2023-24")
                    .status(CaseStatus.OPEN)
                    .client(client1)
                    .assignedLawyer(lawyer1)
                    .build();
            case1 = caseRepository.save(case1);

            Case case2 = Case.builder()
                    .title("XYZ Industries - Patent Infringement")
                    .description("Patent infringement case filed by competitor")
                    .status(CaseStatus.OPEN)
                    .client(client2)
                    .assignedLawyer(lawyer2)
                    .build();
            case2 = caseRepository.save(case2);

            Case case3 = Case.builder()
                    .title("Raj Kumar - Property Dispute")
                    .description("Property ownership dispute with neighbor")
                    .status(CaseStatus.PENDING)
                    .client(client3)
                    .assignedLawyer(lawyer1)
                    .build();
            case3 = caseRepository.save(case3);

            Case case4 = Case.builder()
                    .title("ABC Corporation - Employment Dispute")
                    .description("Former employee wrongful termination claim")
                    .status(CaseStatus.CLOSED)
                    .client(client1)
                    .assignedLawyer(lawyer2)
                    .build();
            case4 = caseRepository.save(case4);

            // Create Hearings
            Hearing hearing1 = Hearing.builder()
                    .date(LocalDateTime.now().plusDays(7))
                    .notes("Initial hearing for GST dispute")
                    .caseEntity(case1)
                    .build();
            hearingRepository.save(hearing1);

            Hearing hearing2 = Hearing.builder()
                    .date(LocalDateTime.now().plusDays(14))
                    .notes("Patent review hearing")
                    .caseEntity(case2)
                    .build();
            hearingRepository.save(hearing2);

            Hearing hearing3 = Hearing.builder()
                    .date(LocalDateTime.now().plusDays(21))
                    .notes("Property documents verification")
                    .caseEntity(case3)
                    .build();
            hearingRepository.save(hearing3);

            // Create Messages
            Message message1 = Message.builder()
                    .sender(lawyer1)
                    .receiver(lawyer2)
                    .content("Please review the documents for ABC Corporation case.")
                    .isRead(false)
                    .build();
            messageRepository.save(message1);

            Message message2 = Message.builder()
                    .sender(lawyer2)
                    .receiver(lawyer1)
                    .content("I have reviewed the documents. We need to discuss the strategy.")
                    .isRead(false)
                    .build();
            messageRepository.save(message2);

            System.out.println("========================================");
            System.out.println("Sample data initialized successfully!");
            System.out.println("========================================");
            System.out.println("Default Credentials:");
            System.out.println("Admin: admin@legalfirm.com / Admin@123");
            System.out.println("Lawyer: john.lawyer@legalfirm.com / Lawyer@123");
            System.out.println("Paralegal: sarah.para@legalfirm.com / Para@123");
            System.out.println("========================================");
        };
    }
}