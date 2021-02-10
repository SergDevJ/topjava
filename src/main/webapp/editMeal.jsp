<%@ page contentType="text/html; charset=UTF-8" pageEncoding= "UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${formTitle}</title>
</head>
<body>
    <h3>${formTitle}</h3>
    <form method="POST" action='meals' name="frmAddMeal">
        <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.MealTo"/>
        Meal ID : <input type="text" readonly="readonly" name="id"
            value="<c:out value="${meal.id}" />" /> <br />
        Description : <input type="text" name="description"
            value="<c:out value="${meal.description}" />" /> <br /> 
        Calories : <input type="number" name="calories"
            value="<c:out value="${meal.calories}" />" /> <br />
        Date&time : <input type="datetime-local" name="dateTime"
<%--            value="${meal.dateTime.format(DateTimeFormatter.ofPattern('dd.MM.yyyy HH:mm'))}" /> <br />--%>
            value="${meal.dateTime}" /> <br />
        <table>
            <tr>
                <th><small>
                    <input type="submit" name="save" value="Save">
                </small>
                <th><small>
                    <input type="submit" name="cancel" value="Cancel">
                </small>
        </table>
    </form>
</body>
</html>