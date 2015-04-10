<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="layout" content="main"/>
        <title>Application Administrators</title>
        <script>
            $(function(){
                $("#addAdmin").on("submit",function(){
                    var ucmnetID=$(".adminAdd").val();
                    if(ucmnetID==""){
                        alert("Please enter a valid UCMNETID");
                        return false;
                    }
                });
                $(".stripeTable .delete").on("click",function(e){
                    var check=confirm("Are you sure? There is no undo.")
                    if(!check){
                        e.preventDefault();
                        return false;
                    }
                })
            });
        </script>
    </head>
    <body>
        <article>
            <h1>Add / Remove Administrators</h1>
            <g:if test="${flash.error}">
                <p class="alert alert-danger">${flash.error}</p>
            </g:if>
            <g:if test="${flash.message}">
                <p class="alert alert-success">${flash.message}</p>
            </g:if>
            <section>
                <g:form name="addAdmin" class="appForm" controller="admin" action="administrators" method="post">
                    <input class="adminAdd" type="text" value="" placeholder="Enter UCMNETID" name="ucmnetid">&nbsp;
                    <button type="submit" class="btn btn-primary">Add User</button>
                </g:form>
            </section>
            <table class="stripeTable" id="administratorsTable">
                <tr><th align="left">UCMNETID</th><th align="center" style="text-align:center;">Remove</th></tr>
                <g:each in="${users}" var="user" status="i">
                    <tr>
                        <td>${user.getUsername()}</td>
                        <td align="center"><a href="${createLink(controller:'admin',action:'administrators',params:[id:user.id])}" class="delete">Delete</a></td>
                    </tr>
                </g:each>
            </table>
        </article>
        <g:adminMenu></g:adminMenu>
    </body>
</html>
