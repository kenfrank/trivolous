<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:basic_page>

<div class="container">
	<div class="row-fluid">
		<div class="span4">
			<h1>Registration Email Sent</h1>
			<div class="well">
				<p>An email was sent to ${email} from notifier@trivolous.com.  Please open the email
				and follow the link inside to complete your registration.  (if you don't see
				it in a few minutes be sure to check your spam folder).</p>
			</div>
		</div>
	</div>
</div>
</t:basic_page>