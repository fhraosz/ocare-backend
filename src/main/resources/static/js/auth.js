/**
 * 인증 관련 유틸리티
 * localStorage를 사용하여 JWT 토큰 및 사용자 정보 관리
 */

const TOKEN_KEY = 'ocare_token';
const USER_KEY = 'ocare_user';

/**
 * 토큰 저장
 */
function saveToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 토큰 가져오기
 */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * 토큰 삭제
 */
function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}

/**
 * 사용자 정보 저장
 */
function saveUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

/**
 * 사용자 정보 가져오기
 */
function getUser() {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
}

/**
 * 사용자 정보 삭제
 */
function removeUser() {
    localStorage.removeItem(USER_KEY);
}

/**
 * 로그인 여부 확인
 */
function isLoggedIn() {
    return !!getToken();
}

/**
 * 인증 필요 페이지 체크
 * 토큰이 없으면 로그인 페이지로 리다이렉트
 */
function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = '/login';
        return false;
    }
    updateNavbar();
    return true;
}

/**
 * 네비게이션 바 업데이트
 */
function updateNavbar() {
    const loggedIn = isLoggedIn();
    const user = getUser();

    // 비로그인 메뉴
    const navLogin = document.getElementById('nav-login');
    const navSignup = document.getElementById('nav-signup');

    // 로그인 메뉴
    const navDashboard = document.getElementById('nav-dashboard');
    const navUser = document.getElementById('nav-user');
    const navLogout = document.getElementById('nav-logout');
    const userNickname = document.getElementById('user-nickname');

    if (loggedIn) {
        // 비로그인 메뉴 숨기기
        if (navLogin) navLogin.classList.add('d-none');
        if (navSignup) navSignup.classList.add('d-none');

        // 로그인 메뉴 표시
        if (navDashboard) navDashboard.classList.remove('d-none');
        if (navUser) navUser.classList.remove('d-none');
        if (navLogout) navLogout.classList.remove('d-none');

        // 사용자 닉네임 표시
        if (userNickname && user) {
            userNickname.textContent = `${user.nickname}님`;
        }
    } else {
        // 비로그인 메뉴 표시
        if (navLogin) navLogin.classList.remove('d-none');
        if (navSignup) navSignup.classList.remove('d-none');

        // 로그인 메뉴 숨기기
        if (navDashboard) navDashboard.classList.add('d-none');
        if (navUser) navUser.classList.add('d-none');
        if (navLogout) navLogout.classList.add('d-none');
    }
}

/**
 * 회원가입 처리
 */
async function handleSignup(event) {
    event.preventDefault();
    clearAlert();

    const name = document.getElementById('name').value.trim();
    const nickname = document.getElementById('nickname').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('password-confirm').value;
    const recordKey = document.getElementById('recordKey').value.trim();

    // 비밀번호 확인
    if (password !== passwordConfirm) {
        showAlert('비밀번호가 일치하지 않습니다.');
        return;
    }

    try {
        const requestBody = { name, nickname, email, password };
        if (recordKey) {
            requestBody.recordKey = recordKey;
        }

        const response = await apiPost('/members/signup', requestBody);

        if (response.success) {
            showAlert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.', 'success');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        }
    } catch (error) {
        showAlert(error.message);
    }
}

/**
 * 로그인 처리
 */
async function handleLogin(event) {
    event.preventDefault();
    clearAlert();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    try {
        const response = await apiPost('/members/login', { email, password });

        if (response.success && response.data) {
            // 토큰 및 사용자 정보 저장
            saveToken(response.data.accessToken);
            saveUser(response.data.member);

            showAlert('로그인 성공!', 'success');
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 500);
        }
    } catch (error) {
        showAlert(error.message);
    }
}

/**
 * 로그아웃 처리
 */
function logout() {
    removeToken();
    removeUser();
    window.location.href = '/login';
}

// 페이지 로드 시 네비게이션 업데이트
document.addEventListener('DOMContentLoaded', updateNavbar);
