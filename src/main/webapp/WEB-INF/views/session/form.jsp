<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>
    <c:choose>
        <c:when test="${not empty sessionItem.id}">Редактирование сеанса</c:when>
        <c:otherwise>Добавление сеанса</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="form-container">
    <form method="post" action="${pageContext.request.contextPath}/sessions/save">
        <c:if test="${not empty sessionItem.id}">
            <input type="hidden" name="id" value="${sessionItem.id}"/>
        </c:if>
        <input type="hidden" name="playId" value="${play.id}"/>

        <div class="form-group">
            <label>Спектакль</label>
            <div class="field-readonly">${play.title}</div>
        </div>

        <div class="form-group">
            <label for="sessionDate">Дата проведения *</label>
            <input type="date" id="sessionDate" name="sessionDate"
                   value="${sessionItem.sessionDate != null ? sessionItem.sessionDate : ''}"
                   required/>
        </div>

        <div class="form-group">
            <label for="sessionTime">Время начала *</label>
            <input type="time" id="sessionTime" name="sessionTime"
                   value="${sessionItem.sessionTime != null ? sessionItem.sessionTime : ''}"
                   required/>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Сохранить</button>
            <a href="${pageContext.request.contextPath}/sessions?playId=${play.id}"
               class="btn btn-secondary">Отмена</a>
        </div>
    </form>
</div>

</div></body></html>