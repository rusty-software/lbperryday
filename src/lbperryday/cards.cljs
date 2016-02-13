(ns lbperryday.cards)

(def no-more-cards {:title "NO MORE CARDS"
                    :body "You have drawn the last card.  Instead of shuffling, please consider creating new cards to play."})

(def victory-card {:title "SWEET, SWEET VICTORY!"
                   :body "Congratulations %s!  You've achieved LBP Nirvana!  Rub it in the faces of your less fortunate compatriots, and perhaps grab them another beverage."})

(def trap-cards [{:audio :scream
                  :title "Darkened Missteps"
                  :body "You've joked about diving from the deck into the pool two stories below, but no one would have expected that you would accidentally step off of the ledge backward, tumbling down the rock cliff face, all because you thought there was a solid step there.  Lose a turn while Sam sutures your back wound and sprays it with antibiotic whiskey."}
                 {:audio :snoring
                  :title "Early To Bed..."
                  :body "Too many Nutter Butters, beef, and beer (and honestly, it was mostly the beer) have combined to give you major sleepy head.  You have passed out early and must lose a turn!  Everyone else MUST DRINK (and optionally snack) or move back a space."}
                 {:audio :rooster
                  :title "...Early To Rise"
                  :body "Thanks to your overindulgence in Nutter Butters, beef, and beer last night, you were first to fall asleep/pass out.  As a consequence, you were the first to awaken.  Feel free to move forward a space while everyone else sleeps it off.  Everyone else MUST NOT DRINK until their next turn."}
                 {:audio :power-down
                  :title "Power Outage"
                  :body "Apparently the owner of the current pimpy LBP abode forgot to pay the electric bill.  Whoever is in first place (including ties) must move back a space while looking for flashlights and beer.  The last place players (including ties) use this opportunity to cheat ahead a space."}
                 {:audio :splash
                  :title "Pool Of Dreams"
                  :body "It's been a LONG time since you've relaxed, much less relaxed while drinking a beer and eating Nutter Butters floating astride a series of strangely phallic pool floaties.  You doze off, and are only startled back to wakefulness when someone yells \"WHAT... are you doing?\"  Lose a turn explaining your mild sunburn and unexpected erection."}])

(def cards [{:title "Sam's Distraction"
             :body "Coldplay decides to play a surprise concert on Friday evening somewhere within a three hour drive of LBP.  If you're playing as Sam, lose a turn unless you're currently eating raw cookie dough, in which case, take another bit of cookie dough and roll again."}
            {:title "STOP LOOKING AT MY CARDS!"
             :body "You get caught looking at Curtis's cards; lose a turn, unless you're playing as Curtis, in which case, take another drink and roll again."}
            {:title "Bill's Naughty Past"
             :body "Bill becomes suspiciously aroused while perusing a copy of \"Bovine Boudoir.\"  If you're playing as Bill, lose a turn, unless you can convince everyone else playing that you really don't miss teat dipping (ruling by simple majority), in which case, take another peak at a magazine and roll again."}
            {:title "40-Yard Dash"
             :body "Drunk Chase and Drunk Phil decide to race down the 40-yard hallway.  Roll the dice twice (if either Chase or Phil are playing, they get to roll) -- highest roll wins the race.  Everyone else must cheer for your preferred stallion. Winner moves forward two spaces, loser moves forward one.  In either case, players for Phil and Chase take a drink.  Anyone that did NOT race or cheer moves back a space."}
            {:title "Conference Calls"
             :body "You show up to LBP completely beaten down, and instead of reveling in the company of your fellow LPBers, you immediately get on a conference call to support a production rollout.  If you're playing as Phil, lose a turn to make the call, unless you say \"FUCK YOU, WORK!\", in which case take a drink and roll again."}
            {:title "Rocket Ship Tito"
             :body "Tony has signed up for Friday morning golf.  Unfortunately for him, Damon(?) forced him to consume ridiculous amounts of various and sundry boozes, and he is forced to sleep like the dead.  If you're playing as Tony, lose a turn, unless both Chase and Rusty can either guilt you into coming, or Damon has been searching for the Man Down, in which case take a drink and roll again."}
            {:title "Return To Your Roots"
             :body "Todd finally convinces the group to return to Possum Kingdom for some real camping.  On the first evening, Todd stubs his toe, splitting it open to a degree that it requires sutures.  If you are playing as Todd and neither Sam nor Po are playing, lose two turns due to infection.  If only Po is playing, lose a turn unless you agree to a vasectomy on the spot.  If Sam is playing, roll again, unless he's gone to see Coldplay this game."}
            {:title "Arts And Crafts"
             :body "Everyone that has legitimately participated in Arts and Crafts activities this year moves forward one space (simple majority judges participation level)."}
            {:title "Man Down!"
             :body "You throw a beer to someone in the pool, but overshoot.  The beer lands somewhere it cannot be found.  Lose a turn unless you're playing as Damon, in which case yell \"I've rescued the Man Down!\" and take another drink and turn."}
            {:title "A New(d) Business Venture"
             :body "Bill and Keith begin talking about what they've been up to this year, and suddenly realize that they're both silent partners in a teat dipping professional services consultancy.  They celebrate their new partnership by playing Civ 5 for the rest of LBP.  Lose a turn if you're playing as either Bill or Keith, unless you can prove you aren't in any business venture together, in which case take a drink and roll again."}
            {:title "Neverending Guac"
             :body "The ingredients are purchased, the house is relatively quiet.  All that's left to do is make the guac!  If you're playing as Garner, lose a turn to make the guac, unless Garner has actually finished the guac by now, in which case eat a chip (preferably with guac), take a drink, and roll again."}
            {:title "BitTorrent Blight"
             :body "Upon connecting your rig to the massive TV, the doorbell rings.  Two men in suits and sunglasses walk in, demanding to see the proof of purchase for all content on your hard drive.  If you are playing as Tai, lose a turn, unless you can convince someone to take the fall for you (or Keith still has your hard drive from last year), in which case take a drink and roll again."}
            {:title "Golfing Through Fire"
             :body "Once again, the annual golf tournament, which was supposed to begin by 10, didn't actually begin until noon.  Lose a turn while suffering from subsequent dehydration, unless you actually played and enjoyed golf this trip, in which case take a drink of WATER to roll again."}
            {:title "Spades For Days"
             :body "The spades games have been more interesting and slightly less friendly than usual this year, resulting in your not having enough money for the buy-in for the poker tournament (or for gas money to get home).  Lose a turn in order to determine what body part to auction off to pay your debts, unless you're playing as Nick, in which case smile wryly and roll again."}
            {:title "Lack Of Reading Material"
             :body "You typically use your time at LBP to read as well as catch up with old friends.  This year, inexplicably, you neglected to pack your book bag.  If you're playing as Rusty, lose a turn searching the house for something worth reading, unless there is alternate reading material (the kind with pictures) within arm's reach, in which case show your favorite to everyone playing and roll again."}
            {:title "Scheduling Conflicts"
             :body "Even though LBP's date was settled six months in advance, Po was unable to attend due to his older and wiser partners scheduling the entire summer off, leaving him on call for three months straight.  Lose a turn if you're playing as Po, unless Po pulled a surprise attendance, in which case you should roll again while either telling the tale of how you managed attendance or taking another drink."}
            {:title "Sleeping Arrangements"
             :body "While you thought you would beat the LBP traffic this year by arriving at noon on Thursday, it turns out everyone else spent Wednesday night in Chase's back yard and got there before you.  Lose a turn trying to figure out where you're going to sleep, unless you're playing as Garner, in which case demonstrate that you can sleep anywhere by pulling any two objects of similar height together and planking on them, and then roll again."}
            {:title "I'm (Not) On A Boat!"
             :body "While out on the lake, you decide to get out of the water to pee for some reason.  While relieving yourself, the boats (because there are two) departing, both thinking that you're on the other one. Lose a turn hitchhiking back to the cabin unless you can spin an entertaining tale of how you beat the boats back (simple majority rules), in which case roll again."}
            {:title "Cheetos And Strife"
             :body "You go looking for two LBP snack staples: cheetos and beer.  Unfortunately, someone has consumed the last of both without notifying the authorities, and you begin to exhibit withdrawal symptoms. Lose a turn making a store run unless you've actually made a store run this LBP, in which case you convince someone else to go and roll again."}
            {:title "One; One-Two; One-One"
             :body "While dropping some mad beatz on the drum machine, Sir Mix-A-Lot bursts through the front door, claiming he heard you riffing and wants to sign you to a record deal.  Lose a turn working out the details of your contract, unless you're playing as Keith, in which case you turn down the deal as you already intend to create your own label (roll again)."}
            {:title "RJ Will Provide"
             :body "Minding your own business after going all-in pre-flop on 3-7o (and winning on a flop that had a 4, turn of 5, and river of 6), Red Phil challenges you to a One-Step.  It quickly escalates to EVERYONE being challenged to One-Step.  Quickly, quote a unique line from \"No Retreat, No Surrender\" (starting with the current player and going clockwise) to help diffuse the situation.  Anyone failing to do so moves back a space, including Phil."}
            {:title "Authorship"
             :body "Chase, so caught up in spinning tales of fancy about what's going to happen at LBP, doesn't realize that it's going on RIGHT NOW.  If you're playing as Chase, lose a turn finishing up your first draft of \"LBP: Untold Tales of Wanton Hedonism,\" unless you were one of the first five people here, in which  case take another drink and roll again."}
            {:title "Bachelorette Party"
             :body "As luck would have it, the house next to LBP has been rented out for a bachelorette party.  If you're playing as Phil, lose a turn walking back to our cabin to put your shirt on, unless you can convince the rest of the players that the bride-to-be's father would be impressed by your manly chest, in which case, take a drink and roll again."}
            {:title "Daytona!"
             :body "Rusty comes through with a set of four full-sized multi-linked Daytona cabinets, only he's having some trouble getting them out of the U-haul and into the house.  If you're playing as Rusty, lose a turn running back an forth with a dolly (not the wooden kind), unless you can convince three other players to help you, in which case you all take a drink and you roll again."}
            {:title "Franklin's Filching"
             :body "Tony, after camping out at Franklin's the night before in order to secure the choicest of brisket, is set upon by brigands en route to LBP.  They make off with the prized beef, leaving Tony with a choice.  If you're playing as Tony, you can either lose a turn going back to Franklin's and waiting it out again, or stop by Rudy's on the way, pick up a replacement, and let everyone be none the wiser, in which case, roll again."}
            {:title "To The Main Stage"
             :body "This year's LBP house is equipped with a stripper pole.  Never being folks to pass up a fantastic opportunity, Keith and Bill set up an \"entertainment service\" starring Sam.  Phil immediately bribes Damon to continue calling Sam to the main stage all night.  If you're playing as Damon, Sam, or Phil, lose a turn while on announcing duties, dancing duties, or being entertained (respectively).  Roll again if you can convince one of the other players to take your place."}
            {:title "Women's [Sporting Event]"
             :body "Lose a turn if any women's sporting event occurs during LBP this year.  Samples include Women's World Cup, Summer Olympics, MMA, Lingerie Football, etc."}
            {:title "The Souls Of LBP"
             :body "Todd gives the most inspirational and compelling sermon ever uttered by mortal man.  It's so convincing that not only do all LBP attendees become Christian, Odin, Zeus, and Osiris show up and convert as well.  The Flying Spaghetti Monster remains unconvinced.  If you're playing as Todd, lose a turn while being congratulated by Jesus.  If Todd actually HAS preached a sermon or converted someone during this LBP, roll again."}
            {:title "The People's Champion"
             :body "Dr. Brewer, after mistakenly ingesting an entire bottle of Boone's Strawberry Hill thinking it was Gatorade, metamorphosizes into CB.  Screaming \"PROLES, UNITE!\", he charges the main stage where Sam is dancing for Phil.  If you're playing as Curtis, lose a turn being restrained by Bill.  If Curtis actually HAS punched someone this LBP, roll again and STOP LOOKING AT HIS CARDS!"}
            {:title "Acute Onset Brisketitis"
             :body "Once again, you have failed to pace yourself, and on the first evening of LBP you are suffering from severe meat sweats.  Lose a turn being stuck in the bathroom all night, unless you missed the first night of this LBP, in which case, roll again (since you certainly didn't get any brisket)."}
            {:title "The Unexpected Chieftain"
             :body "The poker gods are indeed with you this year, as you continue to not only get good cards, but manage to play them correctly for once in your LBP card-playing career.  Roll again unless you are a former or current LBP Chieftain, in which case take another drink and tell us about your worst bad beat ever (or the worst bad beat in your memory of LBP)."}
            {:title "Chose Your Own Adventure"
             :body "Roll a dice.  If the number's parity matches the current LBP instance parity (e.g., you roll a 3 (which is odd), and it's LBP 17 (also odd)), roll again.  If not, move back a space.  The negative outcome can be countered by telling a tale from LBPs past.  If the tale is entertaining enough (as judged by simple player majority), move forward a space instead of rolling again."}
            {:title "Sweat Shop"
             :body "You might not be surprised to find that Phil has brought work with him to LBP.  What will surprise you, however, is that he's been listening to Pastor Todd's \"Power of Persuasion\" sermon series, and has used the skills learned there to enlist assistance.  Lose a turn (and take a drink) helping Phil with TPS reports if you've brought a computer to LBP this year.  If you're playing as Phil, roll again, since Todd's series also has a section on \"delegation.\""}
            {:title "Social Interaction"
             :body "In the history of LBP, no honey has ever actually approached any of the attendees... until now.  Lose a turn trying to think of what to say to her without embarassing yourself too much, unless you're playing as one of the single attendees, in which case give us your smoothest lines, take a drink and roll again."}
            {:title "Hot House of Funk"
             :body "LBP is being held on Lake Whitney for the first time.  Unfortunately, what no one could have guessed is that the air conditioner is unable to get the house any cooler than 80 degrees, even with people farting outside exclusively.  Lose a turn trying to figure out how best to glamp in such sweaty conditions, unless you're playing as Nick, who is too cool to sweat under any circumstances, in which case you must make a toast to everyone and then roll again."}
            {:title "Do You Want To Know A Secret?"
             :body "If you know the context of this question, explain it to the players and roll again; otherwise, lose a turn.  Correct answers are judged by simple majority of the remaining players."}
            ])

