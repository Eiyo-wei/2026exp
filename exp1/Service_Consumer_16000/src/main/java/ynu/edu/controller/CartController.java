package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ynu.edu.entity.Cart;
import ynu.edu.entity.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/getCartById/{userId}")
    public Cart GetCartById(@PathVariable("userId") Integer userId) {

        Cart cart = new Cart();
        //根据用户ID获取用户信息
        User user = restTemplate.getForObject("http://provider-service/user/getUserById/"+userId.toString(), User.class);
        cart.setUser(user);
        List<String> goodList = new ArrayList<>();
        goodList.add("小龙虾");
        goodList.add("无人机");
        goodList.add("笔记本电脑");
        cart.setGoodList(goodList);
        return cart;

    }

}
