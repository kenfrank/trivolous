<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" type="image/png" href="//s3.amazonaws.com/trivolous/img/trivolous_icon_16.png" />

<title>Trivolous</title>

<!-- Le styles -->
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/trivolous.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/global.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/game.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/mediaqueries.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/dropzone.css" rel="stylesheet">


<script	src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

	<jsp:invoke fragment="header"/>
</head>



<body>

    <jsp:doBody/>

	<footer class="row" id="footer">
		<div class="container">
			<div class="col-md-12">
				<p>Copyright &copy; 
				<!-- Keeping year current -->
				<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
				<jsp:useBean id="date" class="java.util.Date" />
				<fmt:formatDate value="${date}" pattern="yyyy" /> 
				Trivolous.com. All rights reserved.</p>
			</div> <!-- .col-md-* -->
		</div> <!-- .container -->
	</footer>



	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="//s3.amazonaws.com/trivolous/bootstrap/js/ie10-viewport-bug-workaround.js"></script>
	
</body>
</html>
