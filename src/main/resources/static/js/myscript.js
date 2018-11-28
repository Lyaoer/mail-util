/**
 * Created by li.yao on 2017/12/14.
 */
function ajax_submit() {
    var data = {};
    data["from"] = $("#from").val();
    data["to"] = $("#to").val();
    data["cc"] = $("#cc").val();
    data["subject"] = $("#subject").val();
    data["body"] = $("#body").val();
    data["files"] = $("#files");
    // data["files"] = JSON$("#files1")
    // var data = '{"from":"hyyang@founder.com.cn","to":"li.yao@founder.com.cn","subject":"1","body":"2"}';
    $.ajax({
        url: "/V1/sendmail",
        dateType: "json",
        contentType: "application/json;charset=utf-8",
        type: "post",
        data: JSON.stringify(data)
    });
}