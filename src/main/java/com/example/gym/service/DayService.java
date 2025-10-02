package com.example.gym.service;

import com.example.gym.entity.Day;
import com.example.gym.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayService {

    @Autowired
    private DayRepository dayRepository;

    public List<Day> getAllDays() {
        return dayRepository.findAllByOrderByOrderIndexAsc();
    }

    public Day getDayById(Long id) {
        return dayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));
    }

    public Day createDay(Day day) {
        if (day.getOrderIndex() == null) {
            List<Day> days = getAllDays();
            int maxOrder = days.stream()
                    .mapToInt(Day::getOrderIndex)
                    .max()
                    .orElse(0);
            day.setOrderIndex(maxOrder + 1);
        }
        return dayRepository.save(day);
    }

    public Day updateDay(Long id, Day dayDetails) {
        Day day = getDayById(id);
        day.setTitle(dayDetails.getTitle());
        day.setDescription(dayDetails.getDescription());
        day.setOrderIndex(dayDetails.getOrderIndex());
        return dayRepository.save(day);
    }

    public void deleteDay(Long id) {
        Day day = getDayById(id);
        dayRepository.delete(day);
    }

    // ✅ Реorder денови
    public List<Day> reorderDays(List<Long> dayIds) {
        for (int i = 0; i < dayIds.size(); i++) {
            Long dayId = dayIds.get(i);
            Day day = getDayById(dayId);
            day.setOrderIndex(i + 1);
            dayRepository.save(day);
        }
        return dayRepository.findAllByOrderByOrderIndexAsc();
    }
}
