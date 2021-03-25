<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%request.setCharacterEncoding("UTF-8");%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr>
    <h2>${action == 'create' ? 'Create user' : 'Edit user'}</h2>
    <jsp:useBean id="user" type="ru.javawebinar.topjava.model.User" scope="request"/>
<%--    <jsp:useBean id="roles" type="ru.javawebinar.topjava.model.Role"/>--%>
    <form method="post" action="save">
        <input type="hidden" name="id" value="${user.id}">
        <dl>
            <dt>Name:</dt> <dd><input type="text" value="${user.name}" minlength="2" maxlength="100" name="name" required></dd>
        </dl>
        <dl>
            <dt>Email:</dt> <dd><input type="text" value="${user.email}" maxlength="100" name="email" required></dd>
        </dl>
        <dl>
            <dt>Password:</dt> <dd><input type="text" value="${user.password}" minlength="5" maxlength="100" name="password" required></dd>
        </dl>
        <dl>
            <dt>Register date:</dt>
            <dd><input type="date" name="registered" value="<fmt:formatDate value="${user.registered}" pattern="yyyy-MM-dd"/>" /></dd>
<%--            <dd><input type="date" value="${user.registered}" name="registered" required></dd>--%>
        </dl>
        <dl>
            <dt>Calories per day:</dt>
            <dd><input type="number" value="${user.caloriesPerDay}" name="caloriesPerDay" max="10000" min="10" required></dd>
        </dl>
        <dl>
            <dt>Enabled:</dt>
            <dd><input type="checkbox" <%=user.isEnabled() ? "checked='checked'" : "" %> name="enabled" value="true"></dd>
<%--            <input type="checkbox" <%=!user.isEnabled() ? "checked='checked'" : "" %> />--%>
        </dl>

        <dl>
            <dt>Roles:</dt>
            <dd><label> USER: <input type="checkbox" name="roles" value="USER" <%=user.getRoles() != null && user.getRoles().contains(ru.javawebinar.topjava.model.Role.valueOf("USER")) ? "checked='checked'" : "" %> /></label></dd>
            <dd><label> ADMIN: <input type="checkbox" name="roles" value="ADMIN" <%=user.getRoles() != null && user.getRoles().contains(ru.javawebinar.topjava.model.Role.valueOf("ADMIN")) ? "checked='checked'" : "" %> /></label></dd>
        </dl>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button">Cancel</button>
    </form>
</section>
</body>
</html>
