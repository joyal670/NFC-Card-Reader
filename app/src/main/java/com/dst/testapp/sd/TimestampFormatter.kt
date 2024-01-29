package com.dst.testapp.sd


class TimestampFormatter {
   /* fun longDateFormat(ts: Timestamp): String
    fun dateTimeFormat(ts: TimestampFull): String
    fun timeFormat(ts: TimestampFull): String*/
    companion object {
        fun longDateFormat(ts: Timestamp): String {
            return  ts.toString()
        }

       fun dateTimeFormat(ts: TimestampFull): String{
           return ts.toString()
       }

       fun timeFormat(start: TimestampFull): String? {
           return  "${start.hour}Hr:${start.minute}mins"
       }

       fun dateFormat(ts: Timestamp) :String {
           return "${ts.day}-${ts.month}-${ts.year}"
       }
   }
}
