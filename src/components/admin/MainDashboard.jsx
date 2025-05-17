import { use, useEffect, useState } from 'react'
import styled from 'styled-components'
import { FileUpload } from '.'
import { SlArrowUp, SlArrowDown } from 'react-icons/sl'
import axios from 'axios'
import { FaStar } from 'react-icons/fa6'
import { PiUserCheckBold } from 'react-icons/pi'
import { PiPencilLineBold } from 'react-icons/pi'
import { RiWechatFill } from 'react-icons/ri'
import ReactApexChart from 'react-apexcharts'

const MainDashboard = () => {
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
      <TitleTop>
        <H1>Navi 대시보드</H1>
      </TitleTop>

      <Container>
        <Top>
          <Left>
            <Wrapper>
              <ChartMainTitle>LLM 평가</ChartMainTitle>
              <div
                style={{
                  flex: 1,
                  minHeight: 0,
                  display: 'flex',
                  marginBottom: '-2rem',
                }}
              >
                <ApexChart reviewSum={reviewSum} reviewCount={reviewCount} />

                <div>
                  <p style={{ fontSize: '0.9rem', margin: '0' }}>세부사항:</p>
                  <p style={{ fontWeight: 'bold', marginRight: '1rem' }}>
                    {Math.floor((reviewSum / (reviewCount * 5)) * 100) > 66 ? (
                      <>
                        LLM이 원활하게
                        <br />
                        답변을 해주고 있어요.
                      </>
                    ) : Math.floor((reviewSum / (reviewCount * 5)) * 100) >
                      33 ? (
                      <>
                        AI의 답변이 전체 맥락을
                        <br />
                        완전히 반영하지 않았을 수 있습니다.
                      </>
                    ) : (
                      <>
                        AI의 답변에 오류가 <br />
                        포함되어 있을 수 있어요.
                      </>
                    )}
                  </p>
                </div>
              </div>
            </Wrapper>
          </Left>
          <Right>
            <Boxes>
              <Wrapper
                $backgroundColor="#FFFEF3"
                $shadowColor="rgba(146, 134, 0, 0.3)"
              >
                <SumItem
                  title="리뷰 평점"
                  color="#F7CE00"
                  value={
                    reviewCount > 0
                      ? `${(reviewSum / reviewCount).toFixed(1)}점`
                      : '리뷰 없음'
                  }
                  icon={<FaStar size="30" color="#fff" />}
                />
              </Wrapper>

              <Wrapper
                $backgroundColor="#F7FAFF"
                $shadowColor="rgba(61, 75, 99, 0.3)"
              >
                <SumItem
                  title="총 사용자 수"
                  color="#71A6FF"
                  value={`${userCount}명`}
                  icon={<PiUserCheckBold size="30" color="#fff" />}
                />
              </Wrapper>
            </Boxes>
            <Boxes>
              <Wrapper
                $backgroundColor="#F8FFFB"
                $shadowColor="rgba(61, 75, 99, 0.3)"
              >
                <SumItem
                  title="총 리뷰 수"
                  color="#63C88E"
                  value={`${reviewCount}개`}
                  icon={<PiPencilLineBold size="30" color="#fff" />}
                />
              </Wrapper>

              <Wrapper
                $backgroundColor="#FBFAFF"
                $shadowColor="rgba(61, 75, 99, 0.3)"
              >
                <SumItem
                  title="누적 질문 수"
                  color="#9E86FF"
                  value={`${totalCount}개`}
                  icon={<RiWechatFill size="30" color="#fff" />}
                />
              </Wrapper>
            </Boxes>
          </Right>
        </Top>
        <Bottom>
          <Left>
            <Wrapper>
              <ChartMainTitle>부서 별 누적 질문 수</ChartMainTitle>
              <DepartmentChart>
                {deptCount.map((item, index) => (
                  <DepartmentChartItem
                    key={index}
                    name={item.department}
                    sum={item.count}
                    percent={Math.floor((item.count / depMaxCount) * 100)}
                    color={departmentColor[index % departmentColor.length]}
                  />
                ))}
                {/* <DepartmentChartItem
                  name="HR"
                  sum="100"
                  percent={100}
                  color="#FFE876"
                />
                <DepartmentChartItem
                  name="IR"
                  sum="80"
                  percent={80}
                  color="#FF8C8C"
                />
                <DepartmentChartItem
                  name="CS"
                  sum="90"
                  percent={90}
                  color="#7AD3A0"
                />
                <DepartmentChartItem
                  name="Sales1"
                  sum="20"
                  percent={20}
                  color="#8DB8FF"
                />
                <DepartmentChartItem
                  name="Sales2"
                  sum="55"
                  percent={55}
                  color="#FFE876"
                />
                <DepartmentChartItem
                  name="QA"
                  sum="98"
                  percent={98}
                  color="#FF8C8C"
                /> */}
              </DepartmentChart>
            </Wrapper>
          </Left>
          <Right>
            <Wrapper>
              <ChartMainTitle>일별 질문 수</ChartMainTitle>
              <div style={{ flex: 1, minHeight: 0 }}>
                <ChartGraph dailyCountData={dailyCount} />
              </div>
            </Wrapper>
          </Right>
        </Bottom>
      </Container>
    </>
  )
}
export default MainDashboard

const DepartmentChartItem = ({ name, sum, percent, color }) => {
  return (
    <DepartmentChartItemWrapper>
      <DepartmentChartItemName>{name}</DepartmentChartItemName>
      <DepartmentChartItemSum $percent={percent} $color={color}>
        {sum}
      </DepartmentChartItemSum>
    </DepartmentChartItemWrapper>
  )
}

const departmentColor = ['#FFE876', '#FF8C8C', '#7AD3A0', '#8DB8FF']

const ChartMainTitle = styled.h2`
  font-size: 1rem;
  margin: 0.5rem 0;
  font-weight: bold;
`
const DepartmentChart = styled.div`
  flex: 1;
  min-height: 0;
  min-width: 0;
  overflow: auto;
  padding-right: 2rem;
`
const DepartmentChartItemWrapper = styled.div``
const DepartmentChartItemName = styled.div`
  font-size: 0.8rem;
  font-weight: bold;
  margin-bottom: 0.3rem;
`
const DepartmentChartItemSum = styled.div`
  width: ${(props) => props.$percent}%;
  background-color: ${(props) => props.$color};
  height: 1.8rem;
  border-radius: 3px 7px 7px 3px;
  font-size: 0.8rem;
  margin-bottom: 0.7rem;

  display: flex;
  justify-content: end;
  padding: 0 0.5rem;
  align-items: center;
  font-weight: bold;
  color: #fff;
`

const SumItem = ({
  title,
  value,
  icon,
  color,
  backgroundColor,
  shadowColor,
}) => {
  return (
    <SumItemWrapper
      color={color}
      backgroundColor={backgroundColor}
      shadowColor={shadowColor}
    >
      <SumItemIcon $color={color}>{icon}</SumItemIcon>
      <SumContents>
        <SumItemTitle>{title}</SumItemTitle>
        <SumItemValue>{value}</SumItemValue>
      </SumContents>
    </SumItemWrapper>
  )
}

const TitleTop = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`
const H1 = styled.h1`
  font-size: 1.3rem;
  margin: 0.7rem 0;
`
const Container = styled.section`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 2rem;
  box-sizing: border-box;
  min-height: 0;

  @media (max-width: 768px) {
    flex-direction: column;
    padding: 0;
    gap: 1rem;
    margin-top: 1rem;
  }
`
const Top = styled.section`
  display: flex;
  flex: 2;
  gap: 2rem;
  box-sizing: border-box;
  min-height: 0;
  min-width: 0;

  @media (max-width: 768px) {
    flex-direction: column;
    padding: 0;
    gap: 1rem;
    margin-top: 1rem;
  }
`
const Bottom = styled.section`
  display: flex;
  flex: 3;
  gap: 2rem;
  box-sizing: border-box;
  min-height: 0;
  min-width: 0;

  @media (max-width: 768px) {
    flex-direction: column;
    padding: 0;
    gap: 1rem;
    margin-top: 1rem;
  }
`
const Left = styled.div`
  display: flex;
  flex: 1;
  min-height: 0;
  min-width: 0;
  // flex-direction: column;
  // padding: 0.8rem 1rem;
  // background-color: #fff;
  // border-radius: 15px;
  // box-shadow: 0px 1px 3px rgba(0, 0, 0, 0.3);
  // max-height: 80vh;
`
const Right = styled.div`
  display: flex;
  flex: 2;
  min-height: 0;
  min-width: 0;
  gap: 2rem;
  flex-direction: column;
  // padding: 0.8rem 1rem;
  // background-color: #fff;
  // border-radius: 15px;
  // box-shadow: 0px 1px 3px rgba(0, 0, 0, 0.3);
  // max-height: 80vh;
`
const Boxes = styled.div`
  display: flex;
  flex: 1;
  min-height: 0;
  gap: 2rem;

  & > div {
    flex-grow: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 1.5rem;
    font-weight: bold;
    // color: #333;
  }
`
const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 0.8rem 1rem;
  background-color: ${(props) => props.$backgroundColor || '#fff'};
  border-radius: 15px;
  box-shadow: 0px 1px 3px
    ${(props) => props.$shadowColor || 'rgba(0, 0, 0, 0.3)'};

  // max-height: 35vh;
  box-sizing: border-box;
  height: 100%;
  width: 100%;
  min-width: 0;
  min-width: 0;
`

const SumItemWrapper = styled.div`
  display: flex;
  width: 100%;
  height: 100%;
  justify-content: space-between;
  align-items: center;
`
const SumItemIcon = styled.div`
  width: 3.2rem;
  height: 3.2rem;
  background: ${(props) => props.$color || '#fff'};
  border-radius: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
`
const SumContents = styled.div`
  justify-items: right;
`
const SumItemTitle = styled.h2`
  font-size: 0.9rem;
  margin: 0 0 0.4rem;
`
const SumItemValue = styled.p`
  font-size: 1.4rem;
  font-weight: bold;
  // color: #333;
  margin: 0;
  padding: 0;
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

const ChartGraph = ({ dailyCountData }) => {
  const fullData = generateDummyData('2025-02-08', '2025-05-14', 0, 200)

  let end = 0
  if (dailyCountData.length > 0) {
    // 데이터가 있을 경우, 마지막 날짜를 마지막 요소 date로 설정
    const lastDate = new Date(dailyCountData[dailyCountData.length - 1].date)
    end = lastDate.getTime()
  } else end = new Date().getTime()
  const start = new Date(end - 6 * 24 * 60 * 60 * 1000).getTime() // 최근 7일

  const [dailyCount, setDailyCount] = useState(
    dailyCountData.map((item) => {
      const date = new Date(item.date).getTime()
      return [date, item.count]
    })
  )

  useEffect(() => {
    setDailyCount(
      dailyCountData.map((item) => {
        const date = new Date(item.date).getTime()
        return [date, item.count]
      })
    )
  }, [dailyCountData])

  useEffect(() => {
    setState({
      series: [
        {
          name: '질문 수',
          data: dailyCount,
        },
      ],
      options: {
        ...state.options,
        xaxis: {
          ...state.options.xaxis,
          min: start,
          max: end,
        },
      },
    })
  }, [dailyCount])

  const [state, setState] = useState({
    series: [
      {
        name: '질문 수',
        data: dailyCount,
      },
    ],
    options: {
      chart: {
        type: 'area',
        height: 350,
        stacked: true,
        zoom: {
          type: 'x',
          enabled: true,
        },
      },
      colors: ['#008FFB'],
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: 'monotoneCubic',
        width: 3,
      },
      fill: {
        type: 'gradient',
        gradient: {
          opacityFrom: 0.6,
          opacityTo: 0.8,
        },
      },
      legend: {
        position: 'top',
        horizontalAlign: 'left',
      },
      xaxis: {
        type: 'datetime',
        min: start,
        max: end,
        labels: {
          formatter: function (val) {
            const date = new Date(val)
            return `${date.getMonth() + 1}/${date.getDate()}`
          },
        },
        tooltip: {
          formatter: (val) => new Date(val).toISOString().split('T')[0],
        },
      },
    },
  })

  return (
    <div style={{ height: '100%' }}>
      <div id="chart" style={{ height: '100%' }}>
        <ReactApexChart
          options={state.options}
          series={state.series}
          type="area"
          height="100%"
        />
      </div>
    </div>
  )
}
