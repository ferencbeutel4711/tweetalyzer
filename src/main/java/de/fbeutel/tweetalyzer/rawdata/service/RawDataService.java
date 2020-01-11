package de.fbeutel.tweetalyzer.rawdata.service;

import de.fbeutel.tweetalyzer.rawdata.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class RawDataService {

    private final RawQuoteRepository rawQuoteRepository;
    private final RawReplyRepository rawReplyRepository;
    private final RawRetweetRepository rawRetweetRepository;
    private final RawStatusRepository rawStatusRepository;
    private final RawUserRepository rawUserRepository;

    public RawDataService(final RawQuoteRepository rawQuoteRepository, final RawReplyRepository rawReplyRepository,
                          final RawRetweetRepository rawRetweetRepository, final RawStatusRepository rawStatusRepository,
                          final RawUserRepository rawUserRepository) {
        this.rawQuoteRepository = rawQuoteRepository;
        this.rawReplyRepository = rawReplyRepository;
        this.rawRetweetRepository = rawRetweetRepository;
        this.rawStatusRepository = rawStatusRepository;
        this.rawUserRepository = rawUserRepository;

    }

    public RawQuote save(final RawQuote rawQuote) {
        return rawQuoteRepository.save(rawQuote);
    }

    public RawReply save(final RawReply rawReply) {
        return rawReplyRepository.save(rawReply);
    }

    public RawRetweet save(final RawRetweet rawRetweet) {
        return rawRetweetRepository.save(rawRetweet);
    }

    public RawStatus save(final RawStatus rawStatus) {
        return rawStatusRepository.save(rawStatus);
    }

    public RawUser save(final RawUser rawUser) {
        return rawUserRepository.save(rawUser);
    }

    public Stream<RawUser> getAllUsers() {
        return rawUserRepository.findAllAsStream();
    }

    public Long getUsersSize() {
        return rawUserRepository.count();
    }

    public Stream<RawStatus> getAllStatus() {
        return rawStatusRepository.findAllAsStream();
    }

    public Long getStatusSize() {
        return rawStatusRepository.count();
    }

    public List<RawStatus> getAllStatusByUserId(final String userId) {
        return rawStatusRepository.findAllByUserId(userId);
    }

    public Stream<RawRetweet> getAllRetweets() {
        return rawRetweetRepository.findAllAsStream();
    }

    public Long getRetweetSize() {
        return rawRetweetRepository.count();
    }

    public List<RawRetweet> getAllRetweetsByUserId(final String userId) {
        return rawRetweetRepository.findAllByUserId(userId);
    }

    public Stream<RawQuote> getAllQuotes() {
        return rawQuoteRepository.findAllAsStream();
    }

    public List<RawQuote> getAllQuotesByReference(final String reference) {
        return rawQuoteRepository.findAllByReference(reference);
    }

    public List<RawRetweet> getAllRetweetsByReference(final String reference) {
        return rawRetweetRepository.findAllByReference(reference);
    }

    public Stream<RawReply> getAllReplies() {
        return rawReplyRepository.findAllAsStream();
    }

    public Long getReplySize() {
        return rawReplyRepository.count();
    }

    public Long getQuotesSize() {
        return rawQuoteRepository.count();
    }
}
