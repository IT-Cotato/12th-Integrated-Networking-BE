package com.cotato.networking.weather.domain.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cotato.networking.weather.domain.test.converter.TestConverter;
import com.cotato.networking.weather.domain.test.dto.request.TestReqDto;
import com.cotato.networking.weather.domain.test.dto.response.TestResDto;
import com.cotato.networking.weather.domain.test.entity.Test;
import com.cotato.networking.weather.domain.test.respository.TestRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TestService {

    private final TestRepository testRepository;

    public TestResDto createPost(TestReqDto request) {
        Test test = TestConverter.toEntity(request);
       /*if (exists) {
            throw new GeneralException(ErrorStatus.PLACE_ALREADY_EXISTS);
        }*/
        testRepository.save(test);
        return TestConverter.toResponse(test);
    }

}
