package com.ironyard.controller;


import com.ironyard.data.Game;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class WarController {



    @RequestMapping(value = "/startgame", method = RequestMethod.GET)
    public String startGame(HttpSession session,
                            @RequestParam(value = "numberOfPlayers", required = false) Integer numberOfPlayers)
    {
        Game aGame = new Game();
        aGame.initGame(numberOfPlayers);
        aGame.dealOutAllCards();
        session.setAttribute("game",aGame);
        return "war";
    }

    @RequestMapping(value = "/turn", method = RequestMethod.GET)
    public String turn(HttpSession session) {
        Game aGame = (Game) session.getAttribute("game");
        aGame.playRound();
        session.setAttribute("winner",aGame.getWinner());
        return "war";
    }
}