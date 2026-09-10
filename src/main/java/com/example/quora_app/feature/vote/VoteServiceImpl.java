package com.example.quora_app.feature.vote;

import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.answer.AnswerRepository;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.QuestionRepository;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import com.example.quora_app.feature.vote.dto.VoteRequest;
import com.example.quora_app.feature.vote.dto.VoteSummaryResponse;
import com.example.quora_app.feature.vote.entity.AnswerVote;
import com.example.quora_app.feature.vote.entity.QuestionVote;
import com.example.quora_app.feature.vote.enums.VoteType;
import com.example.quora_app.feature.vote.repository.AnswerVoteRepository;
import com.example.quora_app.feature.vote.repository.QuestionVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final QuestionVoteRepository questionVoteRepository;
    private final QuestionRepository questionRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository  userRepository;
    private final AnswerRepository answerRepository;
    private final AnswerVoteRepository answerVoteRepository;

    @Override
    @Transactional
    public void voteOnQuestion(UUID questionId, VoteRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->new ResourceNotFoundException("User not found"));
        Question question=questionRepository.findById(questionId).orElseThrow(()->new ResourceNotFoundException("Question not found"));
        QuestionVote vote = questionVoteRepository.findByUserIdAndQuestionId(userId, questionId).orElse(null);
        if (vote == null) {
            vote = QuestionVote.builder()
                    .user(user)
                    .question(question)
                    .type(request.getType())
                    .build();
            questionVoteRepository.save(vote);
            return;
        }
        vote.setType(request.getType());
    }

    @Override
    @Transactional
    public void voteOnAnswer(UUID answerId, VoteRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        AnswerVote vote = answerVoteRepository.findByUserIdAndAnswerId(currentUserId, answerId).orElse(null);
        if (vote == null) {
            vote = AnswerVote.builder()
                    .user(user)
                    .answer(answer)
                    .type(request.getType())
                    .build();
            answerVoteRepository.save(vote);
            return;
        }
        vote.setType(request.getType());
    }

    @Override
    @Transactional
    public void removeVoteFromQuestion(UUID questionId) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        QuestionVote vote = questionVoteRepository.findByUserIdAndQuestionId(currentUserId, questionId).orElseThrow(() -> new ResourceNotFoundException("Vote not found"));
        questionVoteRepository.delete(vote);
    }

    @Override
    @Transactional
    public void removeVoteFromAnswer(UUID answerId) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        AnswerVote vote = answerVoteRepository.findByUserIdAndAnswerId(currentUserId, answerId).orElseThrow(() -> new ResourceNotFoundException("Vote not found"));
        answerVoteRepository.delete(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public VoteSummaryResponse getQuestionVoteSummary(UUID questionId) {
        questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        long upvotes = questionVoteRepository.countByQuestionIdAndType(questionId, VoteType.UPVOTE);
        long downvotes = questionVoteRepository.countByQuestionIdAndType(questionId, VoteType.DOWNVOTE);
        UUID currentUserId = currentUserService.getCurrentUserId();
        VoteType myVote = questionVoteRepository.findByUserIdAndQuestionId(currentUserId, questionId).map(QuestionVote::getType).orElse(null);
        return VoteSummaryResponse.builder()
                .upvotes(upvotes)
                .downvotes(downvotes)
                .myVote(myVote)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VoteSummaryResponse getAnswerVoteSummary(UUID answerId) {
        answerRepository.findById(answerId).orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        long upvotes = answerVoteRepository.countByAnswerIdAndType(answerId, VoteType.UPVOTE);
        long downvotes = answerVoteRepository.countByAnswerIdAndType(answerId, VoteType.DOWNVOTE);
        UUID currentUserId = currentUserService.getCurrentUserId();
        VoteType myVote = answerVoteRepository.findByUserIdAndAnswerId(currentUserId, answerId).map(AnswerVote::getType).orElse(null);
        return VoteSummaryResponse.builder()
                .upvotes(upvotes)
                .downvotes(downvotes)
                .myVote(myVote)
                .build();
    }
}
