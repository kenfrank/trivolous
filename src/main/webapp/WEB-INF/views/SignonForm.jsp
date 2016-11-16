<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:basic_page>
<div class="container">
	<div class="row-fluid">
		<div class="span4">
			<c:if test="${!empty message}">
				<div class="alert">
				  ${message}
				</div>				
			</c:if>

			<h1>Please sign in</h1>
			<div class="well">
				<form class="well" action="<c:url value="/signon"/>" method="POST">
					<p>Please enter your email and password</p>
					<fieldset>
					    <input id="email" type="text" name="email" class="input-large"
							<c:if test="${!empty email}"> value="${email}" readonly </c:if>
					    	placeholder="Email">
					    <input type="password" name="password" class="input-large" placeholder="Password">
					    <label class="checkbox">
						    <input type="checkbox" name="remember"> Remember me
					    </label>
					    <button type="submit" name="update" class="btn btn-primary">Sign in</button>
					    <button type="submit" name="forgot" class="btn btn-small">Oops, I Forgot My Password</button>
				    </fieldset>
				</form>
				<div class="well">
					<p>Or use <a href="${fb_login_url}" class="btn btn-primary">Facebook</a> </p>
				</div>
			</div>
		    
			<h1>New to Trivolous?</h1>
			<form class="well" action="<c:url value="/signon"/>" method="POST">
				<fieldset>
					<input type="hidden" name="gameid" value="${param.gameid}" />
					<input type="hidden" name="gamecode" value="${param.gamecode}" />
					<div class="well">
					    <input id="email" type="text" name="email" class="input-large" value="${param.email}" placeholder="Email">
					    <button type="submit" name="register" class="btn btn-primary">Register Me via Email</button>
					</div>
					<div class="well">
				    	<button type="submit" name="fbregister" class="btn btn-primary">Register Me via Facebook</button>
				    </div>
			    </fieldset>
			</form>
		</div>
	</div>
</div>
</t:basic_page>