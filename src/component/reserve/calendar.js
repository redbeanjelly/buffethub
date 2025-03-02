import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const DateTimePicker = ({ selectedDate, onDateChange }) => {
  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1); // 내일 날짜 설정

  // 오전 9시부터 오후 9시까지의 시간 배열 생성
  const minTime = new Date();
  minTime.setHours(8, 59); // 09:00을 선택하기 위해서
  const maxTime = new Date();
  maxTime.setHours(21, 0); // 21:00

  return (
    <div className="date-time-picker">
      {/* 날짜와 시간을 동시에 선택 */}
      <div className="date-picker ">
        <DatePicker
          selected={selectedDate}
          onChange={onDateChange} // 선택된 날짜 및 시간 변경시 호출
          showTimeSelect // 시간 선택 옵션 활성화
          timeIntervals={60} // 시간 간격 (1시간)
          dateFormat="yyyy-MM-dd h:mm aa" // 날짜와 시간 형식
          inline
          placeholderText="날짜 및 시간을 선택하세요"
          isClearable
          minDate={tomorrow} // 과거 날짜 선택 방지
          minTime={minTime} // 선택 가능한 최소 시간
          maxTime={maxTime} // 선택 가능한 최대 시간
        />
      </div>

      {/* 선택한 날짜와 시간 출력 */}
      
      <div className="pt-5 flex justify-center text-center items-center">
        <div className="bg-customColor5 text-sm flex border-1 border-gray-400 h-16 w-2/5 
        rounded-md justify-center text-center items-center">
          {selectedDate && (
            <p>
              예약일 : {" "}<span className="font-bold text-lg">{selectedDate.toLocaleString()}</span>
            </p>
          )}
        </div>
      </div>
      <p className="text-xs pt-1 text-right">※ 예약날짜를 확인하세요 ※</p>
    </div>
  );
};

export default DateTimePicker;