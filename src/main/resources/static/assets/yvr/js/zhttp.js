// util
// err
// log
(function () {

    var http = {};

    http.constant = {
        method: {
            GET: 'GET',
            POST: 'POST'
        },
        contentType: {
            form: 'application/x-www-form-urlencoded',
            file: 'multipart/form-data'
        },
        comet: {
            endline: '\n-[comet]-\n'
        },
        ajax: {
            useAjaxReturn: true,
            useJson: true
        }
    };

    //================================================ ajax请求部分

    var _ajax, _ajaxDone, _ajaxFail, _ajaxErrorMsg;

    _ajax = function (method, url, form, callback) {
        if (typeof form === 'function') {
            callback = form;
            form = null;
        }
        $.ajax({
            type: method,
            url: url,
            data: form
        }).done(function (re) {
            _ajaxDone(re, callback);
        }).fail(_ajaxFail);
    };

    _ajaxDone = function (re, callback) {
        if (http.constant.ajax.useJson) {
            if (typeof re === 'string') {
                re = $z.fromJson(re);
            } else {
                throw new Error("ajaxReturn is not a String, can't be use as JSON");
            }
        }
        if (http.constant.ajax.useAjaxReturn) {
            if (re.ok) {
                callback(re);
            } else {
                _ajaxErrorMsg(re);
            }
        } else {
            callback(re);
        }
    };

    _ajaxFail = function (e) {
        alert('Ajax Error!\n' + e);
    };

    _ajaxErrorMsg = function (re) {
        alert(re.msg);
    };

    /**
     * 设置ajax成功时,对返回内容的处理方式
     *
     * @param opt
     *
     * {
     *      useAjaxReturn   : true | false,
     *      useJson         : true | false
     * }
     *
     */
    http.setAajx = function (opt) {
        if (opt.useAjaxReturn !== undefined) {
            http.constant.ajax.useAjaxReturn = opt.useAjaxReturn;
        }
        if (opt.useJson !== undefined) {
            http.constant.ajax.useJson = opt.useJson;
        }
    };

    http.get = function (url, form, callback) {
        _ajax(http.constant.method.GET, url, form, callback);
    };

    http.post = function (url, form, callback) {
        _ajax(http.constant.method.POST, url, form, callback);
    };

    http.getText = function (url, form, callback) {
        if (typeof form == "function") {
            callback = form;
            form = null;
        }
        var whenDone = function (text) {
            if (typeof callback == "function") callback(text);
        };
        var ajaxOption = {
            url: url,
            data: form,
            dataType: "text",
            processData: true
        };
        $.ajax(ajaxOption).done(whenDone).fail(_ajaxFail);
    };

    http.syncGet = function (url, form) {
        if (typeof form == "function") {
            callback = form;
            form = null;
        }
        var re;
        $.ajax({
            type: "GET",
            async: false,
            url: url,
            data: form,
            dataType: "text",
            processData: true,
            success: function (text) {
                re = $z.fromJson(text);
            }
        });
        return re;
    }

    //============================================= XMLHttpRequest

    /**
     * 生成一个新的XMLHttpRequest对象, 兼容IE
     *
     * @returns {XMLHttpRequest}
     */
    http.xhr = function () {
        if (window.XMLHttpRequest === undefined) {
            window.XMLHttpRequest = function () {
                // IE5和IE6不支持, 需要使用ActivieX对象进行构建
                try {
                    return new ActiveXObject("Msxml2.XMLHTTP.6.0");
                } catch (e1) {
                    try {
                        return new ActiveXObject("Msxml2.XMLHTTP.3.0");
                    } catch (e2) {
                        throw new Error("XMLHttpRequest is not supported");
                    }
                }
            };
        }
        return new XMLHttpRequest();
    };

    //================================================ http长连接

    /**
     * 在进行长连接前, 对opt做对应的检查与处理
     *
     * @param opt
     */
    var _chkCometOpt = function (opt) {
        // 检查
        if (!opt.url) {
            throw new Error("url is empty");
        }
        if (!$.isFunction(opt.onChange)) {
            throw new Error("onChange is not defined");
        }
        if (!opt.contentType) {
            opt.contentType = http.constant.contentType.form;
        }
        if (!opt.method) {
            opt.method = http.constant.method.GET;
        }
        // 准备
        if (!$.isEmptyObject(opt.data)) {
            var params = [];
            var key;
            for (key in opt.data) {
                params.push(key + "=" + encodeURIComponent(opt.data[key]));
            }
            opt.urlparams = params.join('&');
        }
        opt.useEndline = (opt.endline != undefined);
        opt.useGet = (opt.method.toUpperCase() == http.constant.method.GET);
        if (opt.useGet) {
            opt.body = null;
            if (opt.urlparams) {
                opt.url += '?' + opt.urlparams;
            }
        } else {
            opt.body = opt.urlparams;
        }
    };

    var _onChange = function (respTxt, opt) {
        respTxt = $.trim(respTxt);
        if (respTxt) {
            opt.onChange(respTxt);
        } else {
            // TODO
        }
    };

    /**
     * 使用XHR发起一个长连接
     *
     * @param opt
     *
     *  {
     *      url         : 'http://xxx.xxx.xxx:8080',
     *      method      : 'GET',
     *      contentType : 'application/x-www-form-urlencoded',
     *      data        : {},
     *      endline     : '\n--[comet]--\n',
     *      onChange    : function(changeTxt){
     *          // changeTxt 是每次更新的内容
     *          // 更新文本的完整性根据endline是否设置进行分割
     *      },
     *      onFinish    : function(respTxt){
     *          // respTxt 是全部的返回内容
     *      },
     *      onError     : function(xhr) {
     *          // 发生错误时, 也就是 status != 200
     *      }
     *  }
     *
     */
    http.comet = function (opt) {
        _chkCometOpt(opt);
        // 发起连接
        var xhr = http.xhr();
        var respLength = 0;
        var respTmp = '';
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState >= 3) {
                var respTxt = xhr.responseText.substr(respLength);
                if (respTxt.length > 0) {
                    respLength += respTxt.length;
                    if (opt.useEndline) {
                        var fpos = respTxt.indexOf(opt.endline);
                        if (fpos < 0) {
                            // 没有拿到完整的数据等待下次
                            respTmp = respTxt;
                        } else {
                            // 尝试拼装数据
                            var realRespTxt = respTmp + respTxt.substr(0, fpos);
                            respTmp = respTxt.substr(fpos + opt.endline.length);
                            respTxt = realRespTxt;
                        }
                    }
                    _onChange(respTxt, opt);
                }
            }
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (opt.onFinish) {
                        opt.onFinish($z.trim(xhr.responseText));
                    }
                } else {
                    if (opt.onError) {
                        opt.onError(xhr);
                    } else {
                        throw new Error("http " + xhr.status + "\n" + xhr.responseText);
                    }
                }
            }
        };
        xhr.open(opt.useGet ? "GET" : "POST", opt.url);
        xhr.setRequestHeader('Content-type', opt.contentType);
        xhr.send(opt.body);
    };

    /**
     * 使用EventSource发起长连接
     *
     * @param opt
     */
    http.cometES = function (opt) {
        // 仅仅支持GET
        opt.method = http.constant.method.GET;
        _chkCometOpt(opt);

        if (window.EventSource === undefined) {
            throw new Error("EventSource is not supported");
            // TODO 使用xhr模拟EventSource
        }
        var evts = new EventSource(opt.url);
        evts.onmessage = function (e) {
            var respTxt = e.data;
            _onChange(respTxt, opt);
        };
        evts.onerror = function (e) {
            if (opt.onFinish) {
                opt.onFinish();
            }
            if (opt.onError) {
                opt.onError(e);
            } else {
                throw new Error("EventSource has err, maybe connect is break");
            }
        };
    };

    /**
     * 使用WebSocket发起长连接
     *
     * @param opt
     */
    http.cometWS = function (opt) {
        // TODO
    };

    // =============================================== 解析url

    http.urlArgs = function () {
        var args = {};
        var query = location.search.substr(1);
        var pairs = query.split('&');
        for (var i = 0; i < pairs.length; i++) {
            var pos = pairs[i].indexOf('=');
            if (pos == -1) {
                continue;
            }
            var name = pairs[i].substr(0, pos);
            var value = pairs[i].substr(pos + 1);
            value = decodeURIComponent(value);
            args[name] = value;
        }
        return args;
    };

    http.urlAfter = function () {
        var lo = decodeURIComponent("" + window.location.href);
        var pos = lo.indexOf("#");
        return pos < 0 ? null : lo.substring(pos + 1);
    };


    window.$http = http;
//===================================================================
    if (typeof define === "function" && define.cmd) {
        define("zhttp", ["jquery"], function () {
            return zUtil;
        });
    }
//===================================================================
})();
