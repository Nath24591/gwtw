$(document).ready( function () {
    let table = null;
    $('#dropComp').on('change', function () {
        let compId = $(this).val();

        $("#tableheading").text($( "#dropComp option:selected" ).text());

        if ( $.fn.dataTable.isDataTable( '#dataTable' ) ) {
            table = $('#dataTable').DataTable();
            table.ajax.url("/getallentries?id=" + compId).load();
            table.draw();
        }
        else {
             table = $('#dataTable').DataTable({
                "sAjaxSource": "/getallentries?id=" + compId,
                "sAjaxDataProp": "",
                "order": [[2, "desc"]],
                "bFilter": false,
                "bLengthChange": false,
                "columns": [
                    {"data": "email"},
                    {"data": "tickets"},
                    {"data": "count"},
                ]
            });
        }

    });
});