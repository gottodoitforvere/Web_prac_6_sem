<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>Театры</h1>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<%-- Фильтры поиска спектаклей — на странице Театры по концептуальной модели --%>
<h2>Поиск спектаклей</h2>
<form method="get"
      action="${pageContext.request.contextPath}/theaters/search"
      class="filters">
    <div class="filter-group">
        <label for="directorId">Режиссёр</label>
        <select id="directorId" name="directorId">
            <option value="">— Все —</option>
            <c:forEach var="d" items="${directors}">
                <option value="${d.id}">${d.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="filter-group">
        <label for="actorId">Актёр</label>
        <select id="actorId" name="actorId">
            <option value="">— Все —</option>
            <c:forEach var="a" items="${actors}">
                <option value="${a.id}">${a.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="filter-group">
        <label for="date">Дата</label>
        <input type="date" id="date" name="date"/>
    </div>
    <div class="filter-group">
        <label>&nbsp;</label>
        <button type="submit" class="btn btn-primary">Найти спектакли</button>
    </div>
</form>

<%-- Список театров --%>
<h2>Список театров</h2>
<div class="actions-row">
    <a href="${pageContext.request.contextPath}/theaters/new"
       class="btn btn-success">+ Добавить театр</a>
</div>

<table>
    <thead>
        <tr>
            <th>Название</th>
            <th>Адрес</th>
            <th>Партер</th>
            <th>Балкон</th>
            <th>Бельэтаж</th>
            <th>Итого мест</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="theater" items="${theaters}">
            <tr>
                <td>
                    <a href="${pageContext.request.contextPath}/plays?theaterId=${theater.id}">
                        ${theater.name}
                    </a>
                </td>
                <td>${theater.address}</td>
                <td>${theater.seatsParterre}</td>
                <td>${theater.seatsBalcony}</td>
                <td>${theater.seatsMezzanine}</td>
                <td>${theater.totalSeats}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/theaters/edit?id=${theater.id}"
                       class="btn btn-warning">Редактировать</a>
                    <form method="post"
                          action="${pageContext.request.contextPath}/theaters/delete"
                          style="display:inline"
                          onsubmit="return confirm('Удалить театр «${theater.name}»?');">
                        <input type="hidden" name="id" value="${theater.id}"/>
                        <button type="submit" class="btn btn-danger">Удалить</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty theaters}">
            <tr><td colspan="7">Театры не найдены.</td></tr>
        </c:if>
    </tbody>
</table>

</div></body></html>