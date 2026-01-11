/**
 * 대시보드 기능
 */

let healthChart = null;

/**
 * 대시보드 초기화
 */
function initDashboard() {
    const user = getUser();
    if (user) {
        document.getElementById('welcome-message').textContent =
            `${user.nickname}님, 환영합니다!`;
    }

    // 기본 날짜 설정 (최근 7일)
    const today = new Date();
    const weekAgo = new Date(today);
    weekAgo.setDate(weekAgo.getDate() - 7);

    document.getElementById('end-date').value = formatDate(today);
    document.getElementById('start-date').value = formatDate(weekAgo);

    // 연도 선택 옵션 생성
    const yearSelect = document.getElementById('query-year');
    const currentYear = today.getFullYear();
    for (let year = currentYear; year >= currentYear - 5; year--) {
        const option = document.createElement('option');
        option.value = year;
        option.textContent = `${year}년`;
        yearSelect.appendChild(option);
    }

    // 조회 유형 변경 이벤트
    document.getElementById('query-type').addEventListener('change', handleQueryTypeChange);

    // 초기 데이터 로드
    loadHealthData();
}

/**
 * 조회 유형 변경 처리
 */
function handleQueryTypeChange() {
    const queryType = document.getElementById('query-type').value;
    const dateRangeContainer = document.getElementById('date-range-container');
    const endDateContainer = document.getElementById('end-date-container');
    const yearContainer = document.getElementById('year-container');

    if (queryType === 'daily') {
        dateRangeContainer.classList.remove('d-none');
        endDateContainer.classList.remove('d-none');
        yearContainer.classList.add('d-none');
    } else {
        dateRangeContainer.classList.add('d-none');
        endDateContainer.classList.add('d-none');
        yearContainer.classList.remove('d-none');
    }
}

/**
 * 건강 데이터 로드
 */
async function loadHealthData() {
    const user = getUser();
    if (!user || !user.recordKey) {
        showNoDataMessage('recordKey가 없습니다. 건강 데이터를 먼저 등록해주세요.');
        return;
    }

    const queryType = document.getElementById('query-type').value;

    try {
        let data;
        if (queryType === 'daily') {
            data = await loadDailyData(user.recordKey);
        } else {
            data = await loadMonthlyData(user.recordKey);
        }

        if (data && data.length > 0) {
            updateSummaryCards(data);
            updateChart(data, queryType);
            updateTable(data, queryType);
        } else {
            showNoDataMessage('조회된 데이터가 없습니다.');
        }
    } catch (error) {
        showAlert(error.message);
        showNoDataMessage('데이터 조회 중 오류가 발생했습니다.');
    }
}

/**
 * 일별 데이터 로드
 */
async function loadDailyData(recordKey) {
    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    let endpoint = `/health/daily?recordKey=${encodeURIComponent(recordKey)}`;
    if (startDate && endDate) {
        endpoint += `&startDate=${startDate}&endDate=${endDate}`;
    }

    const response = await apiGet(endpoint);
    return response.success ? response.data : [];
}

/**
 * 월별 데이터 로드
 */
async function loadMonthlyData(recordKey) {
    const year = document.getElementById('query-year').value;

    let endpoint = `/health/monthly?recordKey=${encodeURIComponent(recordKey)}`;
    if (year) {
        endpoint += `&year=${year}`;
    }

    const response = await apiGet(endpoint);
    return response.success ? response.data : [];
}

/**
 * 요약 카드 업데이트
 */
function updateSummaryCards(data) {
    const totalSteps = data.reduce((sum, item) => sum + (item.steps || 0), 0);
    const totalCalories = data.reduce((sum, item) => sum + (item.calories || 0), 0);
    const totalDistance = data.reduce((sum, item) => sum + (item.distance || 0), 0);

    document.getElementById('total-steps').textContent = totalSteps.toLocaleString() + ' 걸음';
    document.getElementById('total-calories').textContent = totalCalories.toFixed(1) + ' kcal';
    document.getElementById('total-distance').textContent = totalDistance.toFixed(2) + ' km';
}

/**
 * 차트 업데이트
 */
function updateChart(data, queryType) {
    const ctx = document.getElementById('health-chart').getContext('2d');

    // 기존 차트 제거
    if (healthChart) {
        healthChart.destroy();
    }

    const labels = data.map(item => queryType === 'daily' ? item.date : item.yearMonth);
    const stepsData = data.map(item => item.steps || 0);
    const caloriesData = data.map(item => item.calories || 0);

    healthChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '걸음 수',
                    data: stepsData,
                    borderColor: 'rgb(13, 110, 253)',
                    backgroundColor: 'rgba(13, 110, 253, 0.1)',
                    tension: 0.3,
                    yAxisID: 'y'
                },
                {
                    label: '칼로리 (kcal)',
                    data: caloriesData,
                    borderColor: 'rgb(220, 53, 69)',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    tension: 0.3,
                    yAxisID: 'y1'
                }
            ]
        },
        options: {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            scales: {
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: '걸음 수'
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: '칼로리 (kcal)'
                    },
                    grid: {
                        drawOnChartArea: false,
                    },
                }
            }
        }
    });
}

/**
 * 테이블 업데이트
 */
function updateTable(data, queryType) {
    const tbody = document.getElementById('data-table-body');

    if (!data || data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted">
                    조회된 데이터가 없습니다.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = data.map(item => `
        <tr>
            <td>${queryType === 'daily' ? item.date : item.yearMonth}</td>
            <td class="text-end">${(item.steps || 0).toLocaleString()}</td>
            <td class="text-end">${(item.calories || 0).toFixed(1)}</td>
            <td class="text-end">${(item.distance || 0).toFixed(2)}</td>
        </tr>
    `).join('');
}

/**
 * 데이터 없음 메시지 표시
 */
function showNoDataMessage(message) {
    document.getElementById('total-steps').textContent = '-';
    document.getElementById('total-calories').textContent = '-';
    document.getElementById('total-distance').textContent = '-';

    document.getElementById('data-table-body').innerHTML = `
        <tr>
            <td colspan="4" class="text-center text-muted">${message}</td>
        </tr>
    `;

    if (healthChart) {
        healthChart.destroy();
        healthChart = null;
    }
}

/**
 * 날짜 포맷팅 (YYYY-MM-DD)
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}
