<%@ page contentType="text/html; charset=UTF-8" pageEncoding= "UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="ru">
<head>
    <title>Meal list</title>
</head>
<body>
    <p><a href="meals?action=insert">Add Meal</a></p>
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
            <c:forEach items="${meals}" var="meal">
                <tr style="background-color:${meal.exceed ? 'greenyellow' : 'red'}">
                    <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${meal.dateTime}" /></td>
                    <td><c:out value="${meal.description}" /></td>
                    <td><c:out value="${meal.calories}" /></td>
                    <td><a href="meals?action=edit&mealId=<c:out value="${meal.id}"/>">Update</a></td>
                    <td><a href="meals?action=delete&mealId=<c:out value="${meal.id}"/>">Delete</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>