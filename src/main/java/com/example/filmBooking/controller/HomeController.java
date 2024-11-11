package com.example.filmBooking.controller;

import com.example.filmBooking.apis.Api;
import com.example.filmBooking.model.Bill;
import com.example.filmBooking.model.Cinema;
import com.example.filmBooking.model.Customer;
import com.example.filmBooking.repository.BillRepository;
import com.example.filmBooking.service.BillService;
import com.example.filmBooking.service.CinemaService;
import com.example.filmBooking.service.CustomerService;
import com.example.filmBooking.service.StatisticsService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    CustomerService customerService;
    @Autowired
    BillRepository billRepository;
    @GetMapping("/admin/login")
    public  String login(){
        return "admin/login";
    }
    @GetMapping("/logout")
    public  String logout(HttpSession session){
        session.removeAttribute("admin");
        return "admin/login";
    }
    @PostMapping("/check")
    public  String checkLogin(Model model,@RequestParam("email") String email,
                              @RequestParam("password") String password,HttpSession session)
    {
        if (customerService.checkAdminLogin(email,password))
        {
            Customer customer = customerService.findCustomerByEmail(email);
            session.setAttribute("admin",customer);
            return "redirect:/";
        }

        else model.addAttribute("ERROR","Wrong answer or password");
        return "/admin/login";
    }
    @GetMapping("")
    public String home(ModelMap model,
                       @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                       @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, HttpSession session ) {

        if(session.getAttribute("admin") == null)
        {
            return "redirect:/admin/login";
        }

        List<BigDecimal> thanhXuan = statisticsService.revenueInTheLast7DaysThanhXuan();
        List<BigDecimal> mipec = statisticsService.revenueInTheLast7DaysMipec();
        List<BigDecimal> myDinh = statisticsService.revenueInTheLast7DaysMyDinh();
        List<Object> revenueTicket = statisticsService.revenueTicket(startDate, endDate);
        List<Object> revenueService = statisticsService.revenueService(startDate, endDate);
        System.out.println(startDate);
        System.out.println(endDate);
        model.addAttribute("revenueTicket", revenueTicket);
        model.addAttribute("revenueService", revenueService);
        model.addAttribute("thanhXuan", thanhXuan);
        model.addAttribute("myDinh", myDinh);
        model.addAttribute("mipec", mipec);
        return "admin/home";
    }
}
