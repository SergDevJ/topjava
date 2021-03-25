<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%request.setCharacterEncoding("UTF-8");%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><spring:message code="meal.title"/></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<section>
    <h3><a href="index.jsp">Home</a></h3>
    <hr/>
    <h2><spring:message code="meal.title"/></h2>

<%--    <h1><"param.startDate1:" <%=request.getParameter("startDate") %>></h1>--%>
<%--    <h1><"param.startDate2:" <%=request.getAttribute("startDate") %>></h1>--%>
<%--    <h1><"param.startDate3: ${startDate}"></h1>--%>

    <form method="get" action="filter">
        <input type="hidden" name="action" value="filter">
        <dl>
            <dt><spring:message code="meal.fromDate"/></dt>
            <dd><input type="date" name="startDate" value="<%=request.getAttribute("startDate") %>"></dd>
        </dl>
        <dl>
            <dt><spring:message code="meal.toDate"/></dt>
            <dd><input type="date" name="endDate" value="${endDate}"></dd>
        </dl>
        <dl>
            <dt><spring:message code="meal.fromTime"/></dt>
            <dd><input type="time" name="startTime" value="${startTime}"></dd>
        </dl>
        <dl>
            <dt><spring:message code="meal.toTime"/></dt>
            <dd><input type="time" name="endTime" value="${endTime}"></dd>
        </dl>
        <button type="submit"><spring:message code="meal.filterTitle"/></button>
    </form>
    <hr/>
    <a href="create">Add Meal</a>
<%--    <a href="meals?action=create">Add Meal</a>--%>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr data-mealExcess="${meal.excess}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
<%--                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>--%>
                <td><a href="update?id=${meal.id}"><spring:message code="common.updateTitle"/></a></td>
<%--                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>--%>
                <td><a href="delete?id=${meal.id}"><spring:message code="common.deleteTitle"/></a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>