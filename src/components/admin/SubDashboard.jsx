import { useEffect, useState } from 'react'
import styled from 'styled-components'
import axios from 'axios'
import ReactApexChart from 'react-apexcharts'

const SubDashboard = () => {
  const [documentList, setDocumentList] = useState([])
  const [editList, setEditList] = useState([])
  const [dailyCount, setDailyCount] = useState([])
  const [deptCount, setDeptCount] = useState([])
  const [totalCount, setTotalCount] = useState(0)
  const [depMaxCount, setDepMaxCount] = useState(0)
  const [userInfo, setUserInfo] = useState([])
  const [userCount, setUserCount] = useState(0)

  const [reviewSum, setReviewSum] = useState(0)
  const [reviewCount, setReviewCount] = useState(0)

  useEffect(() => {
    fetchDocumentList()
    fetchDailyCount()
    fetchDeptCount()
    fetchTotalCount()
    fetchUser()
    // fetchUserReview(3)
  }, [])

  useEffect(() => {
    if (Array.isArray(userInfo) && userInfo.length > 0) {
      setReviewCount(0)
      setReviewSum(0)
      handleReview()
    }
  }, [userInfo])

  const fetchUserReview = (userId) => {
    axios
      .get(`${process.env.REACT_APP_SERVER_URL}/chat/reviews/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
        },
      })
      .then((response) => {
        console.log('사용자 리뷰 가져오기 성공:', response.data)
        const reviews = response.data

        const sum = reviews.reduce(
          (acc, review) => acc + Number(review.rating || 0),
          0
        )
        const count = reviews.length

        // 상태를 안전하게 누적 업데이트
        setReviewSum((prev) => prev + sum)
        setReviewCount((prev) => prev + count)

        console.log('리뷰 합계:', sum)
        console.log('리뷰 개수:', count)
      })
      .catch((error) => {
        console.error('사용자 리뷰 가져오기 오류:', error)
      })
  }

  const handleReview = () => {
    userInfo.forEach((user) => {
      fetchUserReview(user.id)
    })
  }

  const fetchUser = () => {
    axios
      .get(`${process.env.REACT_APP_SERVER_URL}/admin/users`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
        },
      })
      .then((response) => {
        console.log('사용자 목록 가져오기 성공:', response.data)
        setUserInfo(response.data)
        setUserCount(response.data.length)
      })
      .catch((error) => {
        console.error('사용자 목록 가져오기 오류:', error)
        // alert('사용자 목록을 가져오는 중 오류가 발생했습니다.')
      })
  }

  const fetchTotalCount = () => {
    axios
      .get(
        `${process.env.REACT_APP_SERVER_URL}/dashboard/messages/total-count`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
          },
        }
      )
      .then((response) => {
        console.log('총 질문 수 가져오기 성공:', response.data.body)
        setTotalCount(response.data.body)
      })
      .catch((error) => {
        console.error('총 질문 수 가져오기 오류:', error)
        alert('총 질문 수를 가져오는 중 오류가 발생했습니다.')
      })
  }

  const fetchDeptCount = () => {
    axios
      .get(
        `${process.env.REACT_APP_SERVER_URL}/dashboard/messages/department-count`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
          },
        }
      )
      .then((response) => {
        console.log('부서 별 질문 수 가져오기 성공:', response.data.body)
        setDeptCount(response.data.body)
        setDepMaxCount(
          Math.max(...response.data.body.map((item) => item.count))
        )
      })
      .catch((error) => {
        console.error('부서 별 질문 수 가져오기 오류:', error)
        alert('부서 별 질문 수를 가져오는 중 오류가 발생했습니다.')
      })
  }

  const fetchDailyCount = () => {
    axios
      .get(
        `${process.env.REACT_APP_SERVER_URL}/dashboard/messages/daily-count`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
          },
        }
      )
      .then((response) => {
        console.log('일별 질문 수 가져오기 성공:', response.data.body)
        setDailyCount(response.data.body)
      })
      .catch((error) => {
        console.error('일별 질문 수 가져오기 오류:', error)
        alert('일별 질문 수를 가져오는 중 오류가 발생했습니다.')
      })
  }

  const fetchDocumentList = () => {
    axios
      .get(`${process.env.REACT_APP_SERVER_URL}/documents`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
        },
      })
      .then((response) => {
        console.log(response.data)
        if (response.data.code === 0) {
          setDocumentList(response.data.body)
        } else {
          alert('문서 목록을 가져오는 데 실패했습니다.')
        }
      })
      .catch((error) => {
        console.error('문서 목록 가져오기 오류:', error)
        alert('문서 목록을 가져오는 중 오류가 발생했습니다.')
      })
  }

  return (
    <>
      <ChartMainTitle>LLM 평가</ChartMainTitle>
      <div
        style={{
          flex: 1,
          minHeight: 0,
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <ApexChart reviewSum={reviewSum} reviewCount={reviewCount} />
        <p
          style={{
            fontWeight: 'bold',
            marginTop: '-3rem',
            textAlignLast: 'center',
          }}
        >
          {Math.floor((reviewSum / (reviewCount * 5)) * 100) > 66 ? (
            <>
              LLM이 원활하게
              <br />
              답변을 해주고 있어요!
            </>
          ) : Math.floor((reviewSum / (reviewCount * 5)) * 100) > 33 ? (
            <>
              AI의 답변이 전체 맥락을
              <br />
              완전히 반영하지 않았을 수 있습니다!
            </>
          ) : (
            <>
              AI의 답변에 오류가 <br />
              포함되어 있을 수 있어요!
            </>
          )}
        </p>
      </div>
    </>
  )
}
export default SubDashboard

const ChartMainTitle = styled.h2`
  font-size: 1rem;
  margin: 0.5rem 0;
  font-weight: bold;
`

const ApexChart = ({ reviewCount, reviewSum }) => {
  useEffect(() => {
    setState({
      series: [Math.floor((reviewSum / (reviewCount * 5)) * 100) || 0],
      options: {
        ...state.options,
        fill: {
          ...state.options.fill,
          colors: ['#61CAFF'],
        },
      },
    })
  }, [reviewCount, reviewSum])

  const [state, setState] = useState({
    series: [Math.floor((reviewSum / (reviewCount * 5)) * 100) || 0],
    options: {
      chart: {
        type: 'radialBar',
        offsetY: -20,
        sparkline: {
          enabled: true,
        },
      },
      plotOptions: {
        radialBar: {
          hollow: {
            size: '45%', // 👈 값을 작게 할수록 두께는 두꺼워짐
          },
          startAngle: -135,
          endAngle: 135,
          track: {
            background: '#F2F2F2',
            strokeWidth: '97%',
            margin: 5, // margin is in pixels
          },
          dataLabels: {
            name: {
              show: false,
            },
            value: {
              offsetY: -2,
              fontSize: '22px',
            },
          },
        },
      },
      stroke: {
        lineCap: 'round',
      },
      grid: {
        padding: {
          top: -10,
        },
      },
      fill: {
        type: 'solid',
        colors: ['#61CAFF'],
      },
      labels: ['Average Results'],
    },
  })

  return (
    <div style={{ flex: 1, minHeight: 0, minWidth: 0 }}>
      <div style={{ height: '100%' }} id="chart">
        <ReactApexChart
          options={state.options}
          series={state.series}
          type="radialBar"
          height="100%"
          padding="0"
        />
      </div>
      <div id="html-dist"></div>
    </div>
  )
}

const generateDummyData = (startDateStr, endDateStr, min, max) => {
  const data = []
  const start = new Date(startDateStr)
  const end = new Date(endDateStr)

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const timestamp = d.getTime()
    const value = Math.floor(Math.random() * (max - min + 1)) + min
    data.push([timestamp, value])
  }

  return data
}
