<!doctype html>
<html lang="zh-CN" style="font-family: sans-serif;-webkit-text-size-adjust: 100%;-ms-text-size-adjust: 100%;">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <style type="text/css">
        td, th {
            padding: 8px;
            line-height: 1.42857143;
            border-top: 1px solid #ddd;
            border: 1px solid #ddd;
            text-align: center;
            vertical-align: middle;
            position: static;
            display: table-cell;
            float: none;
        }
    </style>
</head>
<body style="margin: 0;">
<table style="background-color: transparent;width: 100%;max-width: 100%;margin-bottom: 20px;border-spacing: 0;border-collapse: collapse;">
    <thead>
    <tr>
        <th style="border-bottom-width: 2px;">贴吧名称</th>
        <th style="border-bottom-width: 2px;">签到结果</th>
    </tr>
    </thead>
    <tbody>
    <#if signInfo?exists>
        <#list signInfo?keys as key>
        <tr>
            <td>${key}</td>
            <#if signInfo[key]>
                <td style="color: green;">
                    签到成功
                </td>
            <#else>
                <td style="color: red;">
                    签到失败
                </td>
            </#if>
        </tr>
        </#list>
    </#if>
    </tbody>
</table>
</body>
</html>