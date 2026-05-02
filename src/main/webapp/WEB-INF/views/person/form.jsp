<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>
    <c:choose>
        <c:when test="${not empty person.id}">Редактирование персоны</c:when>
        <c:otherwise>Добавление персоны</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="form-container">
    <form method="post" action="${pageContext.request.contextPath}/persons/save">
        <c:if test="${not empty person.id}">
            <input type="hidden" name="id" value="${person.id}"/>
        </c:if>

        <div class="form-group">
            <label for="name">Имя *</label>
            <input type="text" id="name" name="name"
                   value="${person.name}" required maxlength="200"/>
        </div>

        <div class="form-group">
            <label for="role">Роль *</label>
            <select id="role" name="role" required>
                <c:forEach var="r" items="${roles}">
                    <option value="${r}"
                        <c:if test="${r == person.role}">selected</c:if>>
                        <c:choose>
                            <c:when test="${r == 'DIRECTOR'}">Режиссёр</c:when>
                            <c:when test="${r == 'ACTOR'}">Актёр</c:when>
                            <c:when test="${r == 'BOTH'}">Режиссёр и актёр</c:when>
                        </c:choose>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Сохранить</button>
            <a href="${pageContext.request.contextPath}/persons" class="btn btn-secondary">Отмена</a>
        </div>
    </form>
</div>

</div></body></html>