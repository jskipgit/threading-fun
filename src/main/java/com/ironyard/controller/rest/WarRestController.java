package com.ironyard.controller.rest;

import com.ironyard.data.Game;
import com.ironyard.dto.ResponseObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wailm.yousif on 3/8/17.
 */

@RestController
@RequestMapping(path = "/rest/cards")
public class WarRestController
{
    private static final String emptyString = "";

    //@Autowired
    //WarRestController() { }

    @RequestMapping(value = "/startgame/{numberOfPlayers}", method = RequestMethod.GET)
    public ResponseObject startGame(@PathVariable Integer numberOfPlayers)
    {
        ResponseObject responseObject = new ResponseObject(false, -1, "Failed to create a new game", null);
        try
        {
            Game aGame = new Game();
            System.out.print("numberOfPlayers=" + numberOfPlayers);

            aGame.initGame(numberOfPlayers);
            aGame.dealOutAllCards();

            responseObject.setSuccess(true);
            responseObject.setResponseCode(0);
            responseObject.setResponseString(emptyString);
            responseObject.setGame(aGame);
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            System.out.println("Exception: " + err);
            responseObject.setResponseString(err);
        }

        System.out.print("Verify:" + responseObject.getResponseString());
        return responseObject;
    }


    @RequestMapping(value = "/turn", method = RequestMethod.POST)
    public ResponseObject turn(Game aGame)
    {
        ResponseObject responseObject = new ResponseObject(false, -2,
                "Failed to play players' round", aGame);
        try
        {
            aGame.playRound();
            responseObject.setSuccess(true);
            responseObject.setResponseCode(0);
            responseObject.setResponseString(emptyString);
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            System.out.println("Exception: " + err);
            responseObject.setResponseString(err);
        }

        return responseObject;
    }
}
