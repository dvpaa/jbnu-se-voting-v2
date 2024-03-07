package jbnu.se.api.service;

import jakarta.transaction.Transactional;
import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.repository.CandidateRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.request.CandidateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    private final HeadquarterRepository headquarterRepository;

    @Transactional
    public void registerCandidate(List<CandidatePair> candidatePairs) {
        candidatePairs.forEach(pairs -> {
            Long headquarterId = pairs.getHeadquarterId();
            Headquarter headquarter = headquarterRepository.findById(headquarterId)
                    .orElseThrow(ElectionNotFoundException::new);

            makeAndSaveCandidateFromRequest(pairs, headquarter);
        });
    }

    private void makeAndSaveCandidateFromRequest(CandidatePair pairs, Headquarter headquarter) {
        CandidateRequest presidentInfo = pairs.getPresident();
        CandidateRequest vicePresidentInfo = pairs.getVicePresident();
        Candidate president = new Candidate(presidentInfo, headquarter);
        Candidate vicePresident = new Candidate(vicePresidentInfo, headquarter);

        candidateRepository.save(president);
        candidateRepository.save(vicePresident);
    }
}
