//存放主要交互逻辑js代码
// javascript 模块化
var seckill = {
    VAL: {
        goodId: 0,
        intervX: 0
    },
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/kill/time/now';
        },
        exposer: function (seckillId) {
            return '/kill/isOpen/' + seckillId;
        },
        execution: function (seckillId, userId) {
            return '/kill/execution/' + seckillId + '/' + userId;
        },
        isGrab: function (seckillId, userId) {
            return '/kill/isGrab/' + seckillId + '/' + userId;
        }
    },
    handleSeckillkill: function (goodId, node) {

        // console.log("test");
        // //获取秒杀地址，控制显示逻辑 ，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');//按钮
        // $.get(seckill.URL.exposer(goodId),
        //     {},
        //     function (result) {
        //         console.log(result['status']);
        //         if(result && result['status']) {
        //             var openStatus = result['data'];
        //             console.log(openStatus['open']);
        //         }
        //     });
        $.get(seckill.URL.exposer(goodId),
            {},
            function (result) {
                //在回调函数中，执行交互流程
                console.log("exposer");
                if (result && result['status']) {
                    var exposer = result['data'];
                    if (exposer['open']) {
                        //开启秒杀
                        //获取秒杀地址.

                        seckill.VAL.goodId = goodId;
                        var userId = $.cookie('userId');
                        var killUrl = seckill.URL.execution(goodId, userId);
                        console.log("killUrl:" + killUrl);
                        //绑定一次点击事件
                        $('#killBtn').one('click', function () {
                            //执行秒杀请求
                            //1:先禁用按钮
                            $(this).addClass('disabled');
                            //2:发送秒杀请求执行秒杀
                            $.get(killUrl, {}, function (result) {
                                console.log(result);
                                if (result && result['status']) {
                                    var killResult = result['data'];
                                    console.log(killResult);
                                    var state = killResult['status'];
                                    console.log(state);
                                    var stateInfo = killResult['description'];
                                    console.log(stateInfo);
                                    //3:显示秒杀结果
                                    node.html('<span class="label label-success">' + stateInfo + '</span>');

                                    if (state == 6) {//排队中
                                        console.log("state=" + state);
                                        seckill.VAL.intervX = window.setInterval(seckill.isGrab, 1000);
                                    }

                                }
                            });
                        });
                        node.show();
                    } else {
                        //未开启秒杀,
                        var now = exposer['now'];
                        var start = exposer['start'];
                        var end = exposer['end'];
                        //重新计算计时逻辑
                        seckill.countdown(goodId, now, start, end);
                    }
                } else {
                    console.log('result:' + result);
                }

            });
    },
    //验证手机号
    validateuserId: function (userId) {
        if (userId && userId.length > 0 && !isNaN(userId)) {
            return true;
        } else {
            return false;
        }
    },
    countdown: function (goodId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        //时间判断
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束!');
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                /*时间完成后回调事件*/
            }).on('finish.countdown', function () {

                seckill.handleSeckillkill(goodId, seckillBox);
            });
        } else {
            //秒杀开始
            //console.log('秒杀开始：goodId：' + goodId);
            seckill.handleSeckillkill(goodId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录 , 计时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var userId = $.cookie('userId');
            //验证手机号
            if(!seckill.validateuserId(userId)) {

                console.log("validate,userId:" + userId);
                //绑定phone
                //控制输出
                var userId = $('#userId');
                //显示弹出层
                userId.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#userIdBtn').click(function () {
                    var inputUserId = $('#userIdTextKey').val();
                    console.log('inputPhone=' + inputUserId);//TODO
                    if (seckill.validateuserId(inputUserId)) {
                        $.cookie('userId', inputUserId, {expires: 1, path: '/kill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#UserIdMessage').hide().html('<label class="label label-danger">ID错误!</label>').show(300);
                    }
                });
            }


            //已经登录
            //计时交互
            var startTime = params['killStartTime'];
            var endTime = params['killEndTime'];
            var goodId = params['goodId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['status']) {
                    var nowTime = result['data'];
                    console.log(nowTime);
                    console.log("goodId=" + goodId);
                    console.log("start=" + startTime);
                    console.log("end=" + endTime);
                    //时间判断,计时交互
                    seckill.countdown(goodId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });


        }
    },
    isGrab: function () {
        var node = $('#seckill-box');
        var userId = $.cookie('userId');
        console.log("isGrab:" + seckill.VAL.goodId);
        $.get(seckill.URL.isGrab(seckill.VAL.goodId, userId),
            {},
            function (result) {
            console.log(result);
                if (result == 0) {
                    console.log(">>>>秒杀排队中...");
                    node.html('<span class="label label-success">' + "排队中..." + '</span>');
                } else {
                    if (seckill.VAL.intervX != 0) {
                        window.clearInterval(seckill.VAL.intervX);
                    }

                    if (result == 1) {
                        console.log(">>>>秒杀成功");
                        node.html('<span class="label label-success">' + "秒杀成功" + '</span>');
                    } else if (result == 2) {
                        console.log(">>>>没抢到！");
                        node.html('<span class="label label-success">' + "没抢到" + '</span>');
                    }
                }

            });
    }

}