package com.palmergames.bukkit.towny.question;

import com.palmergames.util.StringMgmt;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class QuestionManager {
    private int questionsPerPage = 5;

    private String questionFormat = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "%d" + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_GREEN + "%s";

    private String optionFormat = ChatColor.GREEN + "      /%s";

    private String optionEntendedFormat = ChatColor.YELLOW + " : %s";

    private String listFooterFormat = ChatColor.DARK_GRAY + " ---- " + ChatColor.GRAY + "Page: %d/%d " + ChatColor.DARK_GRAY + "~" + ChatColor.GRAY + " Total Questions: %d";

    // All the active questions pending for a player.
    Map<String, LinkedList<AbstractQuestion>> activeQuestions = new HashMap<String, LinkedList<AbstractQuestion>>();

    private static int nextQuestionId = 0;

    public static int getNextQuestionId() {
        return nextQuestionId++;
    }

    public void newQuestion(Question question) {
    }

    public void appendQuestion(Question question) throws Exception {
        if (question.options.size() == 0) {
            throw new Exception("Question has no options.");
        }

        LinkedList<AbstractQuestion> playersActiveQuestions = activeQuestions.get(question.target);
        if (playersActiveQuestions == null) {
            playersActiveQuestions = new LinkedList<AbstractQuestion>();
            activeQuestions.put(question.target.toLowerCase(), playersActiveQuestions);
        }
        playersActiveQuestions.add(question);
        activeQuestions.put(question.target, playersActiveQuestions);

        Player player = Bukkit.getPlayer(question.getTarget());
        if (player != null) {
            for (String line : formatQuestion(question, "New Question")) {
                player.sendMessage(line);
            }
        }
    }

    public List<String> formatQuestion(AbstractQuestion question, String tag) {
        List<String> out = new ArrayList<String>();
        out.add(String.format(this.questionFormat, new Object[]{tag, StringMgmt.maxLength(question.getQuestion(), 54)}));
        for (Option option : question.getOptions()) {
            out.add(String.format(this.optionFormat, new Object[]{option.toString()}) + (option.hasDescription() ? String.format(this.optionEntendedFormat, new Object[]{option.getOptionDescription()}) : ""));
        }
        return out;
    }

    public List<String> formatQuestionList(String user, int page) {
        List<String> out = new ArrayList<String>();
        try {
            if (page < 0) {
                throw new Exception("Invalid page number.");
            }

            LinkedList<AbstractQuestion> activePlayerQuestions = getQuestions(user);
            int numQuestions = activePlayerQuestions.size();
            int maxPage = (int) Math.ceil(numQuestions / (double) questionsPerPage);
            if (page > maxPage) {
                throw new Exception("There are no questions on page " + page);
            } else {
                int start = (page - 1) * questionsPerPage;
                for (int i = start; i < start + questionsPerPage; i++) {
                    try {
                        AbstractQuestion question = activePlayerQuestions.get(i);
                        out.add(String.format(questionFormat, i, StringMgmt.maxLength(question.getQuestion(), 54)));
                        for (Option option : question.getOptions()) {
                            out.add(String.format(optionFormat, option.toString()) + (option.hasDescription() ? String.format(optionEntendedFormat, option.getOptionDescription()) : ""));
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
                if (maxPage > 1) {
                    out.add(String.format(listFooterFormat, page, maxPage, numQuestions));
                }
            }
        } catch (Exception e) {
            out.add(ChatColor.RED + e.getMessage());
        }
        return out;
    }

    public void appendLinkedQuestion(LinkedQuestion question) throws Exception {
        if (question.options.size() == 0) {
            throw new Exception("Question has no options.");
        }

        for (String target : question.targets) {
            LinkedList<AbstractQuestion> playersActiveQuestions = activeQuestions.get(target);
            if (playersActiveQuestions == null) {
                playersActiveQuestions = new LinkedList<AbstractQuestion>();
                activeQuestions.put(target.toLowerCase(), playersActiveQuestions);
            }
            playersActiveQuestions.add(question);
            activeQuestions.put(target, playersActiveQuestions);
        }
    }

    public LinkedList<AbstractQuestion> getQuestions(String target) throws Exception {
        LinkedList<AbstractQuestion> playersActiveQuestions = activeQuestions.get(target.toLowerCase());
        if (playersActiveQuestions == null) {
            throw new Exception("There are no pending questions");
        }
        return playersActiveQuestions;
    }

    public AbstractQuestion peekAtFirstQuestion(String target) throws Exception {
        LinkedList<AbstractQuestion> playersActiveQuestions = getQuestions(target);
        if (playersActiveQuestions.size() == 0) {
            removeAllQuestions(target);
            throw new Exception("There are no pending questions");
        }
        return playersActiveQuestions.peek();
    }

    public void removeAllQuestions(String target) {
        activeQuestions.remove(target.toLowerCase());
    }

    public Runnable answerFirstQuestion(String target, String command) throws InvalidOptionException, Exception {
        return peekAtFirstQuestion(target).getOption(command).reaction;
    }

    /**
     * Remove a question from the top. Recursively removes id if a linked
     * question.
     *
     * @param target
     * @throws Exception
     */
    public void removeFirstQuestion(String target) throws Exception {
        LinkedList<AbstractQuestion> playersActiveQuestions = getQuestions(target);
        if (playersActiveQuestions.size() == 0) {
            removeAllQuestions(target);
            throw new Exception("There are no pending questions");
        }
        if (playersActiveQuestions.peek() instanceof LinkedQuestion) {
            LinkedQuestion question = (LinkedQuestion) playersActiveQuestions.peek();
            int id = question.id;
            for (String qTarget : new ArrayList<String>(question.targets)) {
                removeQuestionId(qTarget, id);
            }
        } else {
            playersActiveQuestions.removeFirst();
        }
    }

    /**
     * Remove a question from at the certain index. Recursively removes id if a
     * linked question.
     *
     * @param target
     * @param queueNumber
     * @throws Exception
     */
    public void removeQuestionInQueue(String target, int queueNumber) throws Exception {
        LinkedList<AbstractQuestion> playersActiveQuestions = getQuestions(target);
        if (playersActiveQuestions.size() == 0) {
            removeAllQuestions(target);
            throw new Exception("There are no pending questions");
        }
        try {
            if (playersActiveQuestions.get(queueNumber) instanceof LinkedQuestion) {
                LinkedQuestion question = (LinkedQuestion) playersActiveQuestions.get(queueNumber);
                int id = question.id;
                for (String qTarget : new ArrayList<String>(question.targets)) {
                    removeQuestionId(qTarget, id);
                }
            } else {
                playersActiveQuestions.removeFirst();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("Invalid question id.");
        }
    }

    /**
     * Remove question of a certain id. Does not recurse for LinkedQuestions.
     *
     * @param target
     * @param id
     * @throws Exception
     */
    public void removeQuestionId(String target, int id) throws Exception {
        LinkedList<AbstractQuestion> playersActiveQuestions = getQuestions(target);
        for (AbstractQuestion question : new LinkedList<AbstractQuestion>(playersActiveQuestions)) {
            if (question.id == id) {
                playersActiveQuestions.remove(question);
            }
        }
    }

    public boolean hasQuestion(String target) {
        try {
            LinkedList<AbstractQuestion> playersActiveQuestions = getQuestions(target);
            if (playersActiveQuestions.size() == 0) {
                removeAllQuestions(target);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
