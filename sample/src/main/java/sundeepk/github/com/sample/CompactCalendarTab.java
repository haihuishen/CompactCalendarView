package sundeepk.github.com.sample;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CompactCalendarTab extends Fragment {

    private static final String TAG = "MainActivity";
    // 日历实例
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    // Locale.getDefault()获取当前的语言环境，把返回值放进SimpleDateFormat的构造里，就能实现通用化，
    // 因此format.format(date)方法返回的值也会根据当前语言来返回对应的值
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private boolean shouldShow = false;                             // false:关闭 true:打开
    private CompactCalendarView compactCalendarView;
    private ActionBar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_tab,container,false);

        final List<String> mutableBookings = new ArrayList<>();

        final ListView bookingsListView = (ListView) v.findViewById(R.id.bookings_listview);
        /** 上个月*/
        final Button showPreviousMonthBut = (Button) v.findViewById(R.id.prev_button);
        /** 下个月*/
        final Button showNextMonthBut = (Button) v.findViewById(R.id.next_button);
        /** 关闭/打开日历*/
        final Button slideCalendarBut = (Button) v.findViewById(R.id.slide_calendar);
        /** 带动画.关闭/打开日历*/
        final Button showCalendarWithAnimationBut = (Button) v.findViewById(R.id.show_with_animation_calendar);
        /** 设置，场景*/
        final Button setLocaleBut = (Button) v.findViewById(R.id.set_locale);
        /** 删除，场景，任何东西*/
        final Button removeAllEventsBut = (Button) v.findViewById(R.id.remove_all_events);


        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mutableBookings);
        bookingsListView.setAdapter(adapter);


        compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);

        // below allows you to configure color for the current day in the month
        // compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        // compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));

        loadEvents();                                                   // 添加事件、项目
        loadEventsForYear(2017);                                        // 添加事件、项目：指定年份
        compactCalendarView.invalidate();                             // 刷新控件

        logEventsByMonth(compactCalendarView);                        // 使用log显示月份的事件

        // below line will display Sunday as the first day of the week
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);

        // disable scrolling calendar
        // compactCalendarView.shouldScrollMonth(false);

        //set initial title
        toolbar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {                                          // 点击"日"
                toolbar.setTitle(dateFormatForMonth.format(dateClicked));                      //
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);       // 拿到"这个日期"存放的事件
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
                if (bookingsFromMap != null) {
                    Log.d(TAG, bookingsFromMap.toString());         // log输出"字符串"数组
                    mutableBookings.clear();
                    for (Event booking : bookingsFromMap) {
                        mutableBookings.add((String) booking.getData());   // 将"事件"存放到;此mutableBookings中
                    }
                    adapter.notifyDataSetChanged();                        // mutableBookings改变了，适配器刷新一下
                }

            }

            // 月份滑动监听
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));    // 得到这个月的第一天
            }
        });

        // "上一个月按钮"
        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();                            // 上个月
            }
        });

        // "下一个月按钮"
        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();                                // 下个月
            }
        });

        // 关闭/打开日历
        slideCalendarBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldShow) {
                    compactCalendarView.showCalendar();                            // 打开日历
                } else {
                    compactCalendarView.hideCalendar();                            // 隐藏日历
                }
                shouldShow = !shouldShow;                                           // 更改当前状态
            }
        });

        // 带动画.关闭/打开日历
        showCalendarWithAnimationBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldShow) {
                    compactCalendarView.showCalendarWithAnimation();                // 打开日历
                } else {
                    compactCalendarView.hideCalendarWithAnimation();                // 隐藏日历
                }
                shouldShow = !shouldShow;
            }
        });

        setLocaleBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = Locale.FRANCE;
                dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", locale);

                //也可以用 getTimeZone 及时区 ID 获取 TimeZone 。例如美国太平洋时区的时区 ID 是 "America/Los_Angeles"。
                // 因此，可以使用下面语句获得美国太平洋时间 TimeZone 对象：TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
                TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");           // TimeZone 表示时区偏移量，也可以计算夏令时。
                dateFormatForDisplaying.setTimeZone(timeZone);
                dateFormatForMonth.setTimeZone(timeZone);

                compactCalendarView.setLocale(timeZone, locale);
                compactCalendarView.setUseThreeLetterAbbreviation(false);

                loadEvents();
                loadEventsForYear(2017);
                logEventsByMonth(compactCalendarView);

            }
        });

        removeAllEventsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.removeAllEvents();
            }
        });

        // uncomment below to show indicators above small indicator events
        // compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        // uncomment below to open onCreate
        //openCalendarOnCreate(v);

        return v;
    }

    private void openCalendarOnCreate(View v) {
        final RelativeLayout layout = (RelativeLayout)v.findViewById(R.id.main_content);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                compactCalendarView.showCalendarWithAnimation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        // toolbar.setTitle(dateFormatForMonth.format(new Date()));
    }

    /**
     * 加载事件、项目
     */
    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);               //  指示在格里高利历和罗马儒略历中一年中第十二个月的 MONTH 字段值。
        addEvents(Calendar.AUGUST, -1);                 //  指示在格里高利历和罗马儒略历中一年中第八个月的 MONTH 字段值。
    }

    /**
     * 加载事件、项目：指定年份
     * @param year  年份
     */
    private void loadEventsForYear(int year) {
        addEvents(Calendar.DECEMBER, year);
        addEvents(Calendar.AUGUST, year);
    }

    /**
     * 使用log显示月份的事件
     * @param compactCalendarView
     */
    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());                                         // 使用给定的 Date 设置此 Calendar 的时间。
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);                              // 将日期设置为： get 和 set 的字段数字，指示一个月中的某天。
        currentCalender.set(Calendar.MONTH, Calendar.AUGUST);                       // 指示月份的 get 和 set 的字段数字。

        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }
        Log.d(TAG, "Events for Aug with simple date formatter: " + dates);
        Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
    }

    /**
     * 添加事件、项目
     * @param month
     * @param year
     */
    private void addEvents(int month, int year) {
        currentCalender.setTime(new Date());                                    // 将当前日期，设为日历当前
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);                         // 将日期设置为： get 和 set 的字段数字，指示一个月中的某天。
        Date firstDayOfMonth = currentCalender.getTime();                       // 获得此时的日期

        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);                           // 设置日历为，当月的第一天
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);                     // 设置日历月份为，month
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);        //  Calendar.ERA:指示年代的 get 和 set 的字段数字，比如罗马儒略历中的 AD 或 BC。
                currentCalender.set(Calendar.YEAR, year);                        // 设置日历年份为，year
            }
                                                                                   // add:根据日历的规则，为给定的日历字段添加或减去指定的时间量。
            currentCalender.add(Calendar.DATE, i);                              // Calendar.DATE 月的第一天 ;

            setToMidnight(currentCalender);                                     // Midnight：午夜，半夜12点钟;;将：时分秒毫秒都设为0(凌晨)
            long timeInMillis = currentCalender.getTimeInMillis();              // 返回此 Calendar 的时间值，以毫秒为单位。


            List<Event> events = getEvents(timeInMillis, i);                       //

            compactCalendarView.addEvents(events);
        }
    }

    /**
     * 添加，listview的数据？
     * 添加，事件.项目
     * @param timeInMillis
     * @param day
     * @return
     */
    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {                                                              // 1
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 4) {
            return Arrays.asList(                                                   // 3和4
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {                                                                    // 2和5
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis) ),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    /**
     * Midnight：午夜，半夜12点钟 <p>
     * 将：时分秒毫秒都设为0(凌晨)
     * @param calendar  要设置的日历
     */
    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);              // get 和 set 的字段数字，指示一天中的小时。
        calendar.set(Calendar.MINUTE, 0);                    // get 和 set 的字段数字，指示一小时中的分钟。
        calendar.set(Calendar.SECOND, 0);                    // get 和 set 的字段数字，指示一分钟中的秒。
        calendar.set(Calendar.MILLISECOND, 0);              // get 和 set 的字段数字，指示一秒中的毫秒。
    }
}