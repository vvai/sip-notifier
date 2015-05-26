<!DOCTYPE html>
<head>
    <title tiles:fragment="title">Login</title>

    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>

<div class="wrapper">
    <form class="form-signin" action="/login" method="post">       
      <%--<h2 class="form-signin-heading">login</h2>--%>
      <input type="text" class="form-control" id="username" name="username" placeholder="ldap login" required="" autofocus="" />
      <input type="password" class="form-control" id="password" name="password" placeholder="password" required=""/>
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>   
    </form>
  </div>

</body>
</html>