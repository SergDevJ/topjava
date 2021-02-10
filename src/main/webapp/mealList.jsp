<%@ page contentType="text/html; charset=UTF-8" pageEncoding= "UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Meal list</title>
</head>
<body>
    <h3>Meal list</h3>
    <p><a href="meals?action=add">Add Meal</a></p>
    <table border=1>
        <thead>
            <tr>
                <th>Date</th>
                <th>Description</th>
                <th>Calories</th>
                <th colspan=2>Action</th>
            </tr>
        </thead>
        <tbody>
            <jsp:useBean id="mealList" scope="request" type="java.util.List"/>
            <c:forEach items="${mealList}" var="meal">
                <tr style="background-color:${meal.excess ? 'red' : 'green'}">
                    <td><c:out value="${meal.dateTime.format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm'))}" /></td>
                    <td><c:out value="${meal.description}" /></td>
                    <td><c:out value="${meal.calories}" /></td>
                    <td><a href="meals?action=edit&id=<c:out value="${meal.id}"/>">Update</a></td>
                    <td><a href="meals?action=delete&id=<c:out value="${meal.id}"/>">Delete</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>