package com.trivolous.game.notify;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.trivolous.game.domain.Member;

import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SMTPDKIMMessage;

public class MySenderEmail implements MySenderInterface, DisposableBean {
	
	
	
	public void setDkimPrivateKeyPath(String dkimPrivateKeyPath) {
		this.dkimPrivateKeyPath = dkimPrivateKeyPath;
	}

	private JavaMailSender mailSender;	
	protected final Log logger = LogFactory.getLog(getClass());
	private ExecutorService executorService = Executors.newFixedThreadPool(10);  // TODO -- revisit this setting.
	private String from;
	private DKIMSigner dkimSigner = null;
	// set to NULL to turn off signing
	private String dkimPrivateKeyPath = null;

 
	private MimeMessage signMessage(MimeMessage mimeMessage) {
		if (dkimPrivateKeyPath != null) {
	    	try {
	    		// just setup signer once.
	    		if (dkimSigner == null) {
					dkimSigner = new DKIMSigner(
							"trivolous.com",
							"default",
							dkimPrivateKeyPath);
					dkimSigner.setIdentity("notifier@trivolous.com");
	    		}
	    		SMTPDKIMMessage signedMsg = new SMTPDKIMMessage(mimeMessage, dkimSigner);
	    		return signedMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mimeMessage;
	}
	
	private class SendRunner implements Runnable
	{
		private final String[] toList; 
		private final String subject;
		private final String textbody;
		private final String htmlbody;
		
		SendRunner(final String[] toList, 
			final String subject, final String textbody, final String htmlbody)
		{
			this.toList = toList; 
			this.subject = subject;
			this.textbody = textbody;
			this.htmlbody = htmlbody;
		}
		
		@Override
		public void run() {
			sendSync(toList, subject, textbody, htmlbody);
		}
		
	}
	
	public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }	

	public void send(final List<Member> toList, 
			final String subject, final String textbody, final String htmlbody) 
	{
		for (Member to : toList) {
			if (to.getEmail() != null) { 
				String[] oneToList = new String[1];
				oneToList[0] = to.getEmail();
				SendRunner sendRunner = new SendRunner(oneToList, subject, textbody, htmlbody);
				executorService.submit(sendRunner);
			}
		}
	}
	
	
	public void sendSync(final String[] toList, 
			final String subject, final String textbody, final String htmlbody) {
		
    	logger.debug("Start sendSync");
    	
       	//logger.info("Classpath="+ System.getProperty("java.class.path"));
        
        try {
        	logger.info("Start email send to:" + Arrays.toString(toList) + " Subject: " + subject);
        	logger.trace(" Body (html): " + htmlbody);
 
        	MimeMessage mimeMessage = mailSender.createMimeMessage();

    		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setTo(toList);
    		helper.setSubject(subject);
    		// could send multipart message with text here, but that seems to increase likelihood of spam filter if they
    		// dont match.  but why would text and html match?  anyway, since only html is used now, just leave it.
    		helper.setText(textbody, htmlbody);  // true for html
    		helper.setReplyTo("notifier@trivolous.com");
    		helper.setFrom(from,"Trivolous"); 

    		MimeMessage signedMsg = signMessage(mimeMessage);
    		
    		mailSender.send(signedMsg);
        	
            logger.debug("End email send");
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());            
        	logger.error("Send Email failed : " + ex.getMessage());
        } catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		executorService.shutdown();
	}
	
}
