package com.periodic.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.request.element.CreateElementRequest;
import com.periodic.backend.domain.request.element.UpdateElementRequest;
import com.periodic.backend.domain.response.element.CreateElementResponse;
import com.periodic.backend.domain.response.element.GetElementResponse;
import com.periodic.backend.domain.response.element.UpdateElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.ElementMapper;
import com.periodic.backend.repository.ElementRepository;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElementService {
	private final Logger log = LoggerFactory.getLogger(ElementService.class);
	private final ElementRepository elementRepository;
	private final ElementMapper elementMapper;
	
	public CreateElementResponse createElement(CreateElementRequest createElementRequest) {
		log.info("Start: Create new element");
		String name = createElementRequest.getName();
		String symbol = createElementRequest.getSymbol();
		if(elementRepository.existsByNameIgnoreCase(name)) {
			log.error("Error: Element with name {} is already exists", name);
			throw new AppException(ErrorCode.ELEMENT_ALREADY_EXISTS);
		}
		if(elementRepository.existsBySymbolIgnoreCase(symbol)) {
			log.error("Error: Element with symbol {} is already exists", symbol);
			throw new AppException(ErrorCode.ELEMENT_ALREADY_EXISTS);
		}
		
		Element element = elementMapper.createElementRequestToElement(createElementRequest);
		Element newElement = elementRepository.save(element);
		CreateElementResponse createElementResponse = elementMapper.elementToCreateElementResponse(newElement);
		log.info("End: Create new element success");
		return createElementResponse;
	}
	
	public PaginationResponse<List<GetElementResponse>> getElements(Pageable pageable, String term) {
		log.info("Start: Get elements start");
		Page<GetElementResponse> pageData = null;
		Page<Element> pageElement = null;
		if(term.isEmpty()) {
			log.info("Find all elements in database");
			pageElement = elementRepository.findAll(pageable);	
		} else {
			log.info("Find elements with name contain term {}", term);
			pageElement = elementRepository.findByNameContainingIgnoreCase(pageable, term);
		}
		pageData = elementMapper.pageElementToPageGetElementResponse(pageElement);
		PaginationResponse<List<GetElementResponse>> response = 
				PaginationUtils.buildPaginationResponse(pageable, pageData);
		log.info("End: Get elements success");
		return response;
	}
	
	public GetElementResponse getElement(long id) {
		log.info("Start: Get element by id {} start", id);
		Element element = findById(id);
		GetElementResponse response = elementMapper.elementToGetElementResponse(element);
		log.info("End: Get element by id {} success", id);
		return response;
	}
	
	private Element findById(long id) {
		log.info("Start: Find element id {} in database", id);
		Element element = elementRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.ELEMENT_NOT_FOUND));
		log.info("End: Find element id {} in database success", id);
		return element;
	}
	
	public UpdateElementResponse updateElement(UpdateElementRequest updateElementRequest, long id) {
		log.info("Start: Update element by id {}", id);
		Element currentElement = findById(id);
		String name = updateElementRequest.getName();
		String symbol = updateElementRequest.getSymbol();
		if(elementRepository.existsByNameIgnoreCase(name) && !name.equals(currentElement.getName())) {
			log.error("Error: Element with name {} is already exists", name);
			throw new AppException(ErrorCode.ELEMENT_ALREADY_EXISTS);
		}
		if(elementRepository.existsBySymbolIgnoreCase(symbol) && !symbol.equals(currentElement.getSymbol())) {
			log.error("Error: Element with symbol {} is already exists", symbol);
			throw new AppException(ErrorCode.ELEMENT_ALREADY_EXISTS);
		}
		
		Element newElement = elementMapper.updateElementRequestToElement(updateElementRequest);
		newElement.setId(id);
		
		Element updatedElement = elementRepository.save(newElement);
		
		UpdateElementResponse response = elementMapper.elementToUpdateElementResponse(updatedElement);
		log.info("End: Update element by id {} success", id);
		return response;
	}
	
}
