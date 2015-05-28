
var saveEventsForm = document.getElementById("saveEventsForm");

$(".auto-call-checkbox").each(function () {
    $(this).change(function (target) {
        var nameAttr = $(this).attr('name');
        var eventId = nameAttr.substring(nameAttr.indexOf("-") + 1);
        var checked = $(this).prop('checked');
        console.log("eventId :", eventId);

        if (!checked) {
            $("#conf-" + eventId).prop('value', "");
        }
        $("#conf-" + eventId).prop('disabled', !checked);


    });
});


function submitSettingsForm() {
    console.log("submit settings form");
    $("#settingsForm").submit();
}

function submitNewEventForm() {
    console.log("submit new event form");
    $("#newEventForm").submit();
}

function setNotifyEvents() {
    console.log("set notify events");
    saveEventsForm.submit();
    /*var eventArray = [];
     var event;
     $(".notify-event").each(function( checkbox ) {

     eventArray.push( {
     uid: this.id,
     checked: this.checked
     } );
     console.log( "piu-piu " + this.id + ": " + this.checked );
     });
     console.log(eventArray);
     $.post("api/events/save",
     JSON.stringify(eventArray),
     function(data, status){
     console.log("done");
     });*/
}