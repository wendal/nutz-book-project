(function ($) {

    var md = {};
    var component = {};

    // 动画相关
    var trans_duration = 350;

    // 各种Class
    var _clz_isFocus = "is-focus";
    var _clz_isDisabled = "is-disabled";
    var _clz_hasValue = "has-value";
    var _clz_hasErr = "has-err";
    var _clz_hasChecked = "has-checked";

    // 辅助函数
    var docBody = function () {
        return $(document.body);
    };

    // text-field
    var textField = {
        functions: {
            'hasValue': function ($input) {
                var cval = $input.val();
                if (cval == undefined || cval == null || cval.trim().length == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        },
        events: {
            'check_input_value': function ($tf) {
                var $input = $tf.find('input');
                if (textField.functions.hasValue($input)) {
                    $tf.addClass(_clz_hasValue);
                } else {
                    $tf.removeClass(_clz_hasValue);
                }
            }
        },
        startListen: function () {
            var $doc = docBody();
            // 选中
            $doc.delegate('.md-text-field', 'focusin', function () {
                var $tf = $(this);
                $tf.siblings().removeClass(_clz_isFocus);
                $tf.addClass(_clz_isFocus);
            });
            // 选出
            $doc.delegate('.md-text-field', 'focusout', function () {
                var $tf = $(this);
                $tf.removeClass(_clz_isFocus);
                // 检查内容
                textField.events.check_input_value($tf);

            });
            // 修改过内容
            $doc.delegate('.md-text-field > input', 'change', function () {
                textField.events.check_input_value($(this).parent());
            });


            // 遍历所有当前页面中的input
            $doc.find('.md-text-field').each(function (i, ele) {
                textField.events.check_input_value($(ele));
            });
        }
    };

    // checkbox-field
    var checkboxField = {
        functions: {},
        events: {
            'toggle_checkbox': function ($chk) {
                var $chkf = $chk.parents('.md-checkbox-field');
                if ($chk.prop('checked')) {
                    $chkf.addClass(_clz_hasChecked);
                } else {
                    $chkf.removeClass(_clz_hasChecked);
                }
            },
            'touch_ripple': function (e) {
                var box = $(this).find('.checkbox-wrap');
                var ele = box[0];
                var $frdiv = box.find('.focus-ripple.ripple-circle');
                if ($frdiv.length > 0) {
                    $frdiv.remove();
                }
                var clickY = ele.offsetHeight / 2;
                var clickX = ele.offsetWidth / 2;
                var height = ele.offsetHeight;
                var width = ele.offsetWidth;
                var size = Math.max(height, width);
                var hsize = size / 2;
                var setX = parseInt(clickX - hsize);
                var setY = parseInt(clickY - hsize);
                // 添加ripple的div
                var rhtml = '';
                rhtml += '<div class="touch-ripple ripple-circle-mini"';
                rhtml += ' style="top: ' + setY + 'px; left: ' + setX + 'px;';
                rhtml += ' width: ' + size + 'px; height: ' + size + 'px;"';
                rhtml += '</div>';
                var $rdiv = $(rhtml);
                box.prepend($rdiv);
                setTimeout(function () {
                    $rdiv.remove();
                }, 2000);
            },
            'focus_ripple_in': function (e) {
                var $cf = $(this);
                if ($cf.hasClass(_clz_isDisabled)) {
                    return false;
                }
                $cf.addClass(_clz_isFocus);
                var box = $cf.find('.checkbox-wrap');
                var boxel = box[0];
                // FIXME chrome下, focus事件优先于click事件100ms以上
                setTimeout(function () {
                    if (box.find('.touch-ripple.ripple-circle').length > 0) {
                        return;
                    }
                    var height = boxel.offsetHeight;
                    var width = boxel.offsetWidth;
                    var size = Math.max(height, width);
                    var setX = 0;
                    var setY = 0;
                    // 添加ripple的div
                    var rhtml = '';
                    rhtml += '<div class="focus-ripple ripple-circle"';
                    rhtml += ' style="top: ' + setY + 'px; left: ' + setX + 'px;';
                    rhtml += ' width: ' + size + 'px; height: ' + size + 'px;"';
                    rhtml += '</div>';
                    var $rdiv = $(rhtml);
                    box.prepend($rdiv);
                }, 200);
            },
            'focus_ripple_out': function (e) {
                var $cf = $(this);
                $cf.removeClass(_clz_isFocus);
                var $rdiv = $cf.find('.focus-ripple.ripple-circle');
                if ($rdiv.length > 0) {
                    $rdiv.remove();
                }
            }
        },
        startListen: function () {
            var $doc = docBody();
            // input 点击
            $doc.delegate('.md-checkbox-field input[type=checkbox]', 'click', function () {
                checkboxField.events.toggle_checkbox($(this));
            });

            // touch
            $doc.delegate('.md-checkbox-field', 'click', checkboxField.events.touch_ripple);
            // focus
            $doc.delegate('.md-checkbox-field', 'focusin', checkboxField.events.focus_ripple_in);
            $doc.delegate('.md-checkbox-field', 'focusout', checkboxField.events.focus_ripple_out);

            // 遍历所有当前页面中的input
            $doc.find('.md-checkbox-field input[type=checkbox]').each(function (i, ele) {
                checkboxField.events.toggle_checkbox($(ele));
            });
        }
    };

    // button
    var button = {
        events: {
            'touch_ripple': function (e) {
                var box = $(this);
                if (box.hasClass(_clz_isDisabled)) {
                    return false;
                }
                var $frdiv = box.find('.focus-ripple.ripple-circle');
                if ($frdiv.length > 0) {
                    $frdiv.remove();
                }
                var x = e.pageX;
                var y = e.pageY;
                var offset = box.offset();
                var clickY = y - offset.top;
                var clickX = x - offset.left;
                var height = this.offsetHeight;
                var width = this.offsetWidth;
                var size = Math.max(height, width);
                var hsize = size / 2;
                var setX = parseInt(clickX - hsize);
                var setY = parseInt(clickY - hsize);
                // 添加ripple的div
                var rhtml = '';
                rhtml += '<div class="touch-ripple ripple-circle"';
                rhtml += ' style="top: ' + setY + 'px; left: ' + setX + 'px;';
                rhtml += ' width: ' + size + 'px; height: ' + size + 'px;"';
                rhtml += '</div>';
                var $rdiv = $(rhtml);
                box.append($rdiv);
                setTimeout(function () {
                    $rdiv.remove();
                }, 2000);
            },
            'focus_ripple_in': function (e) {
                var box = $(this);
                if (box.hasClass(_clz_isDisabled)) {
                    return false;
                }
                var boxel = this;
                // FIXME chrome下, focus事件优先于click事件100ms以上
                setTimeout(function () {
                    if (box.find('.touch-ripple.ripple-circle').length > 0) {
                        return;
                    }
                    var height = boxel.offsetHeight;
                    var width = boxel.offsetWidth;
                    var size = Math.max(height, width);
                    var hsize = size / 2;
                    var move = Math.min(height, width) / 2;
                    var setX = 0;
                    var setY = 0;
                    if (height > width) {
                        setX = -hsize + move;
                        setY = 0;
                    } else if (height < width) {
                        setX = 0;
                        setY = -hsize + move;
                    } else {
                        // 中间咯
                    }
                    // 添加ripple的div
                    var rhtml = '';
                    rhtml += '<div class="focus-ripple ripple-circle"';
                    rhtml += ' style="top: ' + setY + 'px; left: ' + setX + 'px;';
                    rhtml += ' width: ' + size + 'px; height: ' + size + 'px;"';
                    rhtml += '</div>';
                    var $rdiv = $(rhtml);
                    box.append($rdiv);
                }, 200);
            },
            'focus_ripple_out': function (e) {
                var box = $(this);
                var $rdiv = box.find('.focus-ripple.ripple-circle');
                if ($rdiv.length > 0) {
                    $rdiv.remove();
                }
            },
            'close_action_list': function (e) {
                e.stopPropagation();
                var $fab = $(this).parents('.fab-button')
                setTimeout(function () {
                    $fab.removeClass('open');
                }, 400);
            },
            'toggle_action_list': function (e) {
                var $fab = $(this);
                $fab.toggleClass('open');
            }
        },
        startListen: function () {
            var $doc = docBody();
            // focus-ripple
            $doc.delegate('.flat-button', 'focusin', button.events.focus_ripple_in);
            $doc.delegate('.flat-button', 'focusout', button.events.focus_ripple_out);
            $doc.delegate('.raised-button', 'focusin', button.events.focus_ripple_in);
            $doc.delegate('.raised-button', 'focusout', button.events.focus_ripple_out);
            // floating-action button
            $doc.delegate('.fab-button', 'click', button.events.toggle_action_list);
            $doc.delegate('.fab-button-mini', 'click', button.events.close_action_list);
            // touch-ripple
            $doc.delegate('.flat-button', 'click', button.events.touch_ripple);
            $doc.delegate('.raised-button', 'click', button.events.touch_ripple);
            $doc.delegate('.fab-button-inner', 'click', button.events.touch_ripple);
            $doc.delegate('.ripple-button', 'click', button.events.touch_ripple);
        }
    };

    var tab = {
        calcBottomCss: function ($tabs) {
            var tleft = 48;
            $tabs.find('li').each(function (i, ele) {
                var $li = $(ele);
                var tw = ele.offsetWidth;
                $li.attr('tleft', tleft);
                $li.attr('twidth', tw);
                tleft += tw;
            });
        },
        startListen: function () {
            var $doc = docBody();
            // md-tab-group
            $doc.delegate('.md-tab-group > li', 'click', function (e) {
                // 计算距离左边的left, 还有当前块的宽度
                var $li = $(this);
                if ($li.hasClass('active')) {
                    return;
                }
                $li.siblings().removeClass('active');
                $li.addClass('active');

                if (!$li.attr('twidth')) {
                    // 计算一下当前group的width与left
                    tab.calcBottomCss($li.parent());
                }

                var $bottom = $li.parent().find('.tab-bottom');
                $bottom.css({
                    'width': $li.attr('twidth') + 'px',
                    'left': $li.attr('tleft') + 'px'
                });

            });
        }
    };


    // 注册所有的控件
    component.textField = textField;
    component.button = button;
    component.tab = tab;
    component.checkboxField = checkboxField;

    // MD的方法
    md.startListen = function () {
        for (var cnm in component) {
            component[cnm].startListen();
        }
    };

    // 在dom加载完毕后开始监控
    $(document).ready(function () {
        md.startListen();
    });

    window.$md = md;

    if (typeof define === "function" && define.cmd) {
        define("md", ["jquery"], function () {
            return $md;
        });
    }

})(jQuery)
