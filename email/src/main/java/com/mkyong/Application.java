package com.mkyong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Application implements CommandLineRunner {

	// https://docs.spring.io/spring/docs/5.1.6.RELEASE/spring-framework-reference/integration.html#mail
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${text}")
	String text;

	@Value("${subject}")
	String subject;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {

		try {
			List<String> receivers = new ArrayList();
			String errors = "";
			File myObj = new File("mail.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				receivers.add(myReader.nextLine());
			}
			myReader.close();
			// sendEmail();
			int batchNum = 2;
			int start = 1000*(batchNum-1);
			start = 1255;
			for (int i = start; i < Math.min(1000*batchNum,receivers.size()) ; i++) {
				try {
					System.out.println(
							"Progress ".concat(String.valueOf(i+1)).concat("/").concat(String.valueOf(receivers.size())));
					System.out.println(receivers.get(i));
					sendEmailWithAttachment(receivers.get(i));
				} catch (Exception e) {
					System.err.println("Error sending mail to: ".concat(receivers.get(i)));
					e.printStackTrace();
					errors = errors.concat(receivers.get(i).concat("\n"));
				}
			}
			
			System.out.println("Errors:");
			System.out.println(errors);
			BufferedWriter writer = new BufferedWriter(new FileWriter("errors_".concat(String.valueOf(batchNum)).concat(".txt"), true));
		    writer.append(errors);
		    writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Done");

	}

	void sendEmail() {

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo("1@gmail.com", "2@yahoo.com");

		msg.setSubject("Testing from Spring Boot");
		msg.setText("Hello World \n Spring Boot Email");

		javaMailSender.send(msg);

	}

	void sendEmailWithAttachment(String to) throws MessagingException, IOException {

		System.out.println("Sending Email to ".concat(to));

		MimeMessage msg = javaMailSender.createMimeMessage();

		// true = multipart message
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);

		helper.setSubject(subject);

		// default = text/plain
		// helper.setText("Check attachment for image!");

		// true = text/html
		helper.setText(text, true);

		// FileSystemResource file = new FileSystemResource(new
		// File("classpath:android.png"));

		// Resource resource = new ClassPathResource("android.png");
		// InputStream input = resource.getInputStream();

		// ResourceUtils.getFile("classpath:android.png");

		helper.addAttachment("MAD.pdf", new ClassPathResource("MAD.pdf"));

		javaMailSender.send(msg);

	}
}
