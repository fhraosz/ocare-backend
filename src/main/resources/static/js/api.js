/**
 * API 호출 유틸리티
 */
const API_BASE_URL = '/api';

/**
 * 공통 fetch 래퍼
 */
async function apiCall(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;

    const defaultHeaders = {
        'Content-Type': 'application/json',
    };

    // JWT 토큰이 있으면 Authorization 헤더 추가
    const token = getToken();
    if (token) {
        defaultHeaders['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
    };

    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || '요청 처리 중 오류가 발생했습니다.');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

/**
 * GET 요청
 */
function apiGet(endpoint) {
    return apiCall(endpoint, { method: 'GET' });
}

/**
 * POST 요청
 */
function apiPost(endpoint, body) {
    return apiCall(endpoint, {
        method: 'POST',
        body: JSON.stringify(body),
    });
}

/**
 * 알림 메시지 표시
 */
function showAlert(message, type = 'danger') {
    const container = document.getElementById('alert-container');
    if (!container) return;

    container.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
}

/**
 * 알림 메시지 제거
 */
function clearAlert() {
    const container = document.getElementById('alert-container');
    if (container) {
        container.innerHTML = '';
    }
}
