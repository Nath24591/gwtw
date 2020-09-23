function format ( d ) {
    // `d` is the original data object for the row
    return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
        '<tr>'+
        '<td>Tickets:</td>'+
        '<td>'+d.tickets+'</td>'+
        '</tr>'+
        '</table>';
}

$(document).ready( function () {
    let table = $('#example').DataTable({
        "sAjaxSource": "/gettodos",
        "sAjaxDataProp": "",
        "order": [[ 0, "asc" ]],
        "bFilter": false,
        "bLengthChange": false,
        "columns": [
            {
                "className":      'details-control',
                "orderable":      false,
                "data":           null,
                "defaultContent": ''
            },
            { "data": "compName"},
            { "data": { "open": "open" },
                render: function (data) {
                    if(data.open == 1){
                        return "Ongoing";
                    }
                    if(data.open == 0){
                        return "Closed";
                    }
                }
            },

        ]
    });
    // Add event listener for opening and closing details
    $('#example tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            row.child( format(row.data()) ).show();
            tr.addClass('shown');
        }
    } );
} );