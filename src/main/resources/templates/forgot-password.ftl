<style>.btn {
        border: 1px solid #1FC157;
        display: inline-block;
        font-weight: 600;
        margin: 0px;
        padding: 5px 10px;
        text-align: center;
        text-transform: uppercase;
        transition: all 0.4s ease 0s;
        width: 30%;
        border-radius: 8px;
        background: #1FC157;
        color: #fff;
        height: 20px;
        font-size: 16px;
    }</style>
<table id="bodyTable" border="0" width="100%" cellspacing="0" cellpadding="0">
    <tbody>
    <tr>
        <td align="center" valign="top">
            <table id="emailContainer" border="0" width="600" cellspacing="0" cellpadding="20">
                <tbody>
                <tr>
                    <td align="center" valign="top">
                        <table id="emailHeader" border="0" width="100%" cellspacing="0" cellpadding="20">
                            <tbody>
                            <tr>
                                <td align="center" valign="top"><img src="https://www.gonewiththewin.co.uk/img/logo/gwtwlogo.png" alt="logo" width="160" height="50" /></td>
                            </tr>
                            <tr>
                                <td align="center" valign="top">Hi ${recipientName}! Click below to reset your password</td>
                            </tr>
                            <tr>
                                <td align="center" valign="top"><a href="https://www.gonewiththewin.co.uk/reset?token=${token}" target="_blank" class="btn">Reset password</a></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>