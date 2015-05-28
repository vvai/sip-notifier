<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Notifier</title>

    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <!--<link rel="stylesheet" href="css/bootstrap-theme.min.css">-->
    <link rel="stylesheet" href="../css/style.css">


</head>
<body>

<!-- Navigation -->
<nav class="navbar navbar-fixed-top" role="navigation">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">
                <img src="../../img/baterfly.png" alt="">
            </a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <h2>${user.getName()} events
                <small>
                    (
${user.getSipNumber() > 0 ? user.getSipNumber() : "you don't set sip phone"})
        </small>

                <button type="button" class="btn btn-sm btn-primary pull-right" data-toggle="modal" data-target=".bs-example-modal-sm">Settings</button>

                <div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-sm">
                        <div class="modal-content">
                <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Settings</h4>
        </div>
                <div class="modal-body">
        <form id="settingsForm" class="form-signin" action="/settings" method="post">
        <div class="form-group">
        <label for="sipNumber">Sip number</label>
        <input type="text" class="form-control" id="sipNumber"
        name="sip-number"
        value=${user.getSipNumber() > 0 ? user.getSipNumber() : 'none'}
        placeholder="1234">
        </div>
                <div class="checkbox">
                <label>
                        <input type="checkbox" name="notified"
                ${user.isNotified() ? "checked" : ""}> Notify
                </label>
                </div>
                </form>
                </div>
                <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" onclick="submitSettingsForm()" class="btn btn-primary">Save changes</button>
                </div>
                </div>
        </div>
        </div>


        </h2>


        </div>
        <!-- /.navbar-collapse -->

        <!-- Small modal -->


        </div>


        <!-- /.container -->
                </nav>


        <!-- Page Content -->
        <div class="container">
        <div class="row">
        <div class="col-lg-12">

        <c:choose>
        <c:when test="${empty events}">
        <h1>You not have events</h1>
        </c:when>

        <c:otherwise>
        <form id="saveEventsForm" class="form-horizontal" action="/save-events" method="post">
        <table class="table table-striped">
        <tr>
        <th class="col-md-3">Date</th>
        <th class="col-md-5">Description</th>
        <th class="col-md-1">Notify</th>
        <th class="col-md-3">Auto Call</th>
        </tr>

        <c:forEach var="event" items="${events}">
        <tr>
        <td><c:out value="${event.getFormatDate()}"/> </td>
        <td><c:out value="${event.getDescription()}"/> </td>
        <td><input class="notify-event" type="checkbox" name="notify-${event.getId()}" value="checked"
        ${event.isNotified() ? "checked" : ""}> </td>
        <td class="form-inline"><input type="checkbox" class="auto-call-checkbox" name="auto-${event.getId()}" ${event.isAutoCall() ? "checked" : ""} value="checked" />
        <input type="text" id="conf-${event.getId()}" class="conf-number form-control input-sm"
        name="conf-${event.getId()}" value="${event.getConferenceNumber() != 0 ? event.getConferenceNumber() : ''}" ${event.isAutoCall() ? "" : "disabled"}/>
        </td>
        </tr>
        </c:forEach>
        </table>
        <button type="button" class="btn btn-sm btn-primary" data-toggle="modal" data-target=".bs-new-event-modal-sm">Add new event</button>
        <button type="button" onclick="setNotifyEvents()" class="btn btn-default pull-right">Save</button>
        </form>
        </c:otherwise>
        </c:choose>


        </div>
        </div>
        </div>

        <%--modal window add new event--%>
        <div class="modal fade bs-new-event-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel">Add new event</h4>
                    </div>
                    <div class="modal-body">
                        <form id="newEventForm" class="form-signin" action="/new-event" method="post">
                            <div class="form-group">
                                <label for="description">Description</label>
                                <input type="text" class="form-control" id="description"
                                       name="description"
                                               placeholder="my super important meeting">
                            </div>
                            <div class="form-group">
                                <label for="date">Date and time</label>
                                <input type="text" class="form-control" id="date"
                                       name="date-time"
                                       placeholder="2015-14-09 12:30:00">
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        <button type="button" onclick="submitNewEventForm()" class="btn btn-primary">Save changes</button>
                    </div>
                </div>
            </div>
        </div>
        <%--end modal window--%>

        <footer class="footer">
                <div class="container">
        <p class="text-muted">Ericpol 2015</p>
        </div>
        </footer>


        <script src="../js/jquery-1.11.2.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>
        <script src="../js/scripts.js"></script>
        </body>
        </html>