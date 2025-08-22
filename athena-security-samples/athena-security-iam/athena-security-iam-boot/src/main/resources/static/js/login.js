/**
 * 显示指定的Tab
 * @param {string} tabId - 要显示的tab内容区域的ID
 */
function showTab(tabId) {
    // 隐藏所有 tab 内容
    const tabContents = document.querySelectorAll('.tab-content');
    tabContents.forEach(tab => {
        tab.classList.remove('active');
    });

    // 移除所有 tab 按钮的激活状态
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.classList.remove('active');
    });

    // 显示选中的 tab 内容
    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.classList.add('active');
    }

    // 激活对应的 tab 按钮
    const selectedButton = document.querySelector(`[onclick="showTab('${tabId}')"]`);
    if (selectedButton) {
        selectedButton.classList.add('active');
    }
}

/**
 * 发送短信验证码
 * 该函数用于向用户输入的手机号发送验证码，并在发送后启动60秒倒计时
 */
function sendSmsCode() {
    const mobile = document.getElementById('mobile').value;
    if (!mobile) {
        alert('请输入手机号码');
        return;
    }

    const button = document.querySelector('.sms-button');
    let countdown = 60;

    // 禁用按钮并开始倒计时
    button.disabled = true;
    button.style.opacity = '0.7';
    button.textContent = `${countdown}秒后重试`;

    const timer = setInterval(() => {
        countdown--;
        if (countdown <= 0) {
            clearInterval(timer);
            button.disabled = false;
            button.style.opacity = '1';
            button.textContent = '获取验证码';
        } else {
            button.textContent = `${countdown}秒后重试`;
        }
    }, 1000);

    // TODO: 调用发送验证码的 API
    fetch('/captcha/sms', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({mobile})
    });
}

/**
 * 加载图形验证码
 * @param {number} retryCount - 重试次数，默认为3次
 */
function loadCaptcha(retryCount = 3) {
    const captchaImage = document.getElementById('captchaImage');
    const uuid = generateUUID();
    document.getElementById('uuid').value = uuid;

    // 设置验证码图片加载失败时的处理逻辑
    captchaImage.onerror = function () {
        if (retryCount > 0) {
            console.log(`验证码加载失败，剩余重试次数：${retryCount}`);
            setTimeout(() => loadCaptcha(retryCount - 1), 1000);
        } else {
            console.error('验证码加载失败');
            alert('验证码加载失败，请刷新页面重试');
        }
    };

    captchaImage.src = `/captcha/image?uuid=${uuid}`;
}

/**
 * 生成UUID字符串
 * @returns {string} 返回一个符合UUID格式的随机字符串
 */
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

/**
 * 执行登录操作
 * @param {string} formId - 登录表单的ID
 */
function login(formId) {
    const form = document.getElementById(formId);
    if (!form) {
        console.error('表单不存在:', formId);
        return;
    }

    // 直接提交表单，让浏览器自动处理重定向
    form.method = "POST";
    form.action = "/rest/login";
    form.submit();

    // 禁用提交按钮，防止重复提交
    const submitButton = form.querySelector('button[type="submit"]');
    if (submitButton) {
        submitButton.disabled = true;
    }
}

// 页面加载完成后自动显示第一个 tab 并加载验证码
document.addEventListener('DOMContentLoaded', () => {
    showTab('username-login');
    loadCaptcha();
});
