/**
 * 本文件将提供 Nutz-JS 最基本的帮助函数定义支持，是 Nutz-JS 所有文件都需要依赖的基础JS文件
 *
 * @author  zozoh(zozohtnt@gmail.com)
 * 2012-10 First created
 */
(function () {
//===================================================================
    var zUtil = {
        //.............................................
        // 得到函数体的代码
        getFuncBodyAsStr: function (func) {
            var str = func.toString();
            var posL = str.indexOf("{");
            var re = $.trim(str.substring(posL + 1, str.length - 1));
            // Safari 会自己加一个语句结尾，靠
            if(re[re.length-1] == ";")
                return re.substring(0, re.length-1);
            return re;
        },
        //.............................................
        // 调用某对象的方法，如果方法不存在或者不是函数，无视
        invoke: function (obj, funcName, args, me) {
            if (obj) {
                var func = obj[funcName];
                if (typeof func == 'function') {
                    func.apply(me || obj, args);
                }
            }
        },
        //.............................................
        // 打开一个新的窗口
        openUrl: function (url, target) {
            var html = '<form target="' + (target || '_blank') + '" method="GET"';
            html += ' action="' + url + '" style="display:none;">';
            html += '</form>';
            var jq = $(html).appendTo(document.body);
            jq[0].submit();
            jq.remove();
        },
        // 模拟POST提交
        postForm: function (url, data) {
            var html = '';
            html += '<form action="' + url + '" method="POST" style="display:none;">';
            for (var nm in data) {
                html += '<input type="text" name="' + nm + '" value="' + data[nm] + '">';
            }
            html += '</form>';
            var jq = $(html).appendTo(document.body);
            jq[0].submit();
            jq.remove();
        },
        //.............................................
        // 返回一个时间戳，其它应用可以用来阻止浏览器缓存
        timestamp: function () {
            return ((new Date()) + '').replace(/[ :\t*+()-]/g, '').toLowerCase();
        },
        //.............................................
        // 扩展第一个对象，深层的，如果遇到重名的对象，则递归
        extend: function (a, b) {
            if (_.isObject(a) && _.isObject(b)) {
                for (var key in b) {
                    var av = a[key];
                    if (_.isArray(av) || _.isFunction(av) || _.isDate(av) || _.isRegExp(av)) {
                        a[key] = b[key];
                    } else if (_.isObject(av)) {
                        this.extend(av, b[key]);
                    } else {
                        a[key] = b[key];
                    }
                }
                return a;
            }
            // 否则不能接受
            throw "can not extend a:" + a + " by b:" + b;
        },
        //.............................................
        winsz: function () {
            if (window.innerWidth) {
                return {
                    width: window.innerWidth,
                    height: window.innerHeight
                };
            }
            if (document.documentElement) {
                return {
                    width: document.documentElement.clientWidth,
                    height: document.documentElement.clientHeight
                };
            }
            return {
                width: document.body.clientWidth,
                height: document.body.clientHeight
            };
        },
        //.............................................
        // json : function(obj, fltFunc, tab){
        //     // toJson
        //     if(typeof obj == "object"){
        //         return JSON.stringify(obj, fltFunc, tab);
        //     }
        //     // fromJson
        //     if (!obj) {
        //         return null;
        //     }
        //     return JSON.parse(obj, fltFunc);
        // },
        //.............................................
        toJson: function (obj, fltFunc, tab) {
            return JSON.stringify(obj, fltFunc, tab);
        },
        //.............................................
        fromJson: function (str, fltFunc) {
            if (!str)
                return null;
            return JSON.parse(str, fltFunc);
        },
        //.............................................
        // 获得当前系统当前浏览器中滚动条的宽度
        // TODO 代码实现的太恶心，要重构!
        scrollBarWidth: function () {
            if (!window.SCROLL_BAR_WIDTH) {
                var newDivOut = "<div id='div_out' style='position:relative;width:100px;height:100px;overflow-y:scroll;overflow-x:scroll'></div>";
                var newDivIn = "<div id='div_in' style='position:absolute;width:100%;height:100%;'></div>";
                var scrollWidth = 0;
                $('body').append(newDivOut);
                $('#div_out').append(newDivIn);
                var divOutS = $('#div_out');
                var divInS = $('#div_in');
                scrollWidth = divOutS.width() - divInS.width();
                $('#div_out').remove();
                $('#div_in').remove();
                window.SCROLL_BAR_WIDTH = scrollWidth;
            }
            return window.SCROLL_BAR_WIDTH;
        },
        // 返回当前时间
        currentTime: function (date) {
            date = date || new Date();
            return zUtil.dateToYYMMDD(date) + " " + zUtil.dateToHHMMSS(date);
        },
        // 返回当前时分秒
        dateToYYMMDD: function (date) {
            date = date || new Date();
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            return year + "-" + zUtil.alignLeft(month, 2, '0') + "-" + zUtil.alignLeft(day, 2, '0');
        },
        // 返回当前年月日
        dateToHHMMSS: function (date) {
            date = date || new Date();
            var hours = date.getHours()
            var minutes = date.getMinutes();
            var seconds = date.getSeconds();
            return zUtil.alignLeft(hours, 2, '0') + ":" + zUtil.alignLeft(minutes, 2, '0') + ":" + zUtil.alignLeft(seconds, 2, '0');
        },
        // 任何东西转换为字符串
        anyToString: function (obj) {
            if (_.isUndefined(obj) || _.isNull(obj)) {
                return "";
            }
            if (_.isString(obj)) {
                return obj;
            }
            if (_.isNumber(obj)) {
                return "" + obj;
            }
            if (_.isObject(obj)) {
                return zUtil.toJson(obj);
            }
            // TODO 补全其他类型
            zUtil.noImplement();
        },
        // 补全右边
        alignRight: function (str, length, char) {
            str = zUtil.anyToString(str);
            if (str.length >= length) {
                return str;
            }
            return str + zUtil.dupString(char, length - str.length);
        },
        // 补全左边
        alignLeft: function (str, length, char) {
            str = zUtil.anyToString(str);
            if (str.length >= length) {
                return str;
            }
            return zUtil.dupString(char, length - str.length) + str;
        },
        // 重复字符
        dupString: function (char, num) {
            if (!char || num < 1) {
                return "";
            }
            var str = "";
            for (var i = 0; i < num; i++) {
                str += char;
            }
            return str;
        },
        // 未实现
        noImplement: function () {
            throw new Error("Not implement yet!");
        }
    };

    // log
    zUtil.logConf = {
        enable: true,               // 是否启动log输出
        trace: false,               // 是否显示调用trace
        showTime: true,             // 是否打印时间,
        showMS: true                // 是否显示毫秒
    };

    zUtil.log = function (log) {
        if (zUtil.logConf.enable) {
            var logPrefix = "";
            // 显示时间点
            if (zUtil.logConf.showTime) {
                logPrefix += '---- ';
                var date = new Date();
                logPrefix += zUtil.currentTime(date);
                if (zUtil.logConf.showMS) {
                    logPrefix += "." + date.getMilliseconds();
                }
                logPrefix += ' ----\n';
            }
            console.debug(logPrefix + log);
            if (zUtil.logConf.trace) {
                console.trace();
            }
        }
    };

// 创建 NutzUtil 的别名
    window.$z = zUtil;
//===================================================================
    if (typeof define === "function" && define.cmd) {
        define("zutil", ["underscore"], function () {
            return zUtil;
        });
    }
//===================================================================
})();