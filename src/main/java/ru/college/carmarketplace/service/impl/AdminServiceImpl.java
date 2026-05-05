package ru.college.carmarketplace.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.college.carmarketplace.model.requests.CarDataCreateRequest;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.CarParameterDTO;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.model.entities.*;
import ru.college.carmarketplace.model.requests.CarDataUpdateRequest;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.repo.OrderRepository;
import ru.college.carmarketplace.service.AdminService;
import ru.college.carmarketplace.service.MinioService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Car> create(CarDTO carDTO, MultipartFile[] mainImages,
                                      MultipartFile[] exclusiveImages, MultipartFile[] recommendImages) throws IOException {
        // Сначала загружаем изображения
        Map<String, List<String>> imagesMap = new HashMap<>();

        imagesMap.put("main", uploadImages(mainImages));
        imagesMap.put("exclusives", uploadImages(exclusiveImages));
        imagesMap.put("recommends", uploadImages(recommendImages));

        carDTO.setImageUrl(imagesMap);
        Car car = convertToProduct(carDTO);

        Car createdCar = carRepository.save(car);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> tryCreatingCar(CarDataCreateRequest carDataCreateRequest){
        try {

            CarDTO carDTO = buildCarDTOFromJson(
                    carDataCreateRequest.getTitle(), carDataCreateRequest.getBrand(), carDataCreateRequest.getModel(), carDataCreateRequest.getPrice(),
                    carDataCreateRequest.getEngineType(), carDataCreateRequest.getTransmission(),
                    carDataCreateRequest.getDriveType(), carDataCreateRequest.getBodyType(),
                    carDataCreateRequest.getYear(), carDataCreateRequest.getEngineVolume(), carDataCreateRequest.getCapacity(), carDataCreateRequest.getMileage(),
                    carDataCreateRequest.getExclusives(), carDataCreateRequest.getRecommends(), carDataCreateRequest.getLocation(), carDataCreateRequest.getParametersJson()
            );

            create(
                    carDTO,
                    carDataCreateRequest.getMainFiles(),
                    carDataCreateRequest.getExclusiveFiles(),
                    carDataCreateRequest.getRecommendFiles()
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (JsonProcessingException e) {

            return ResponseEntity.badRequest().body("Invalid parameters JSON");
        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public void deleteProduct(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public void updateCar(Long id, CarDTO carDTO, MultipartFile[] mainFiles,
                          MultipartFile[] exclusiveFiles, MultipartFile[] recommendFiles,
                          List<String> oldMain, List<String> oldExclusives,
                          List<String> oldRecommends) {

        Map<String, List<String>> imagesMap = new HashMap<>();

        checkOldImages(imagesMap, oldMain, oldExclusives, oldRecommends);

        addNewImagesToMap(imagesMap, mainFiles, exclusiveFiles, recommendFiles);

        carDTO.setImageUrl(imagesMap);
        updateProduct(id, carDTO);
    }

    private void checkOldImages(Map<String, List<String>> imagesMap, List<String> oldMain, List<String> oldExclusives,
                               List<String> oldRecommends){
        if (oldMain != null) imagesMap.put("main", oldMain);
        if (oldExclusives != null) imagesMap.put("exclusives", oldExclusives);
        if (oldRecommends != null) imagesMap.put("recommends", oldRecommends);
    }

    private void addNewImagesToMap(Map<String, List<String>> imagesMap, MultipartFile[] mainFiles,
                                   MultipartFile[] exclusiveFiles, MultipartFile[] recommendFiles) {
        try {
            addImagesToMap(imagesMap, "main", mainFiles);
            addImagesToMap(imagesMap, "exclusives", exclusiveFiles);
            addImagesToMap(imagesMap, "recommends", recommendFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> tryUpdateCar(Long id,
                                          CarDataUpdateRequest updateRequest){
        try {
            CarDTO carDTO = buildCarDTOFromJson(
                    updateRequest.getTitle(), updateRequest.getBrand(),
                    updateRequest.getModel(), updateRequest.getPrice(),
                    updateRequest.getEngineType(), updateRequest.getTransmission(),
                    updateRequest.getDriveType(), updateRequest.getBodyType(),
                    updateRequest.getYear(), updateRequest.getEngineVolume(),
                    updateRequest.getCapacity(), updateRequest.getMileage(),
                    updateRequest.getExclusives(),
                    updateRequest.getRecommends(), updateRequest.getLocation(),
                    updateRequest.getParametersJson()
            );

            List<String> oldMain = parseJsonList(updateRequest.getOldMainJson());
            List<String> oldExclusives = parseJsonList(updateRequest.getOldExclusivesJson());
            List<String> oldRecommends = parseJsonList(updateRequest.getOldRecommendsJson());

            updateCar(
                    id,
                    carDTO,
                    updateRequest.getMainFiles(),
                    updateRequest.getExclusiveFiles(),
                    updateRequest.getRecommendFiles(),
                    oldMain,
                    oldExclusives,
                    oldRecommends
            );
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {

            return ResponseEntity.badRequest().body("Invalid JSON format");
        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }


    private List<String> parseJsonList(String json) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    @Override
    public Map<String, Object>  getCarById(Long id) {
        CarDTO carDto = carRepository.findById(id).map(CarDTO::getDto).orElseThrow(() -> new EntityNotFoundException("Car not foumd"));
        Map<String, Object> response = new HashMap<>();
        response.put("car", carDto);
        response.put("imageUrl", carDto.getImageUrl());
        return response;
    }

    @Override
    public Page<CarDTO> getAllCars(Long search, Pageable pageable) {
        if (search != null) {
            return carRepository.findById(search, pageable).map(CarDTO::getDto);
        }else {
            return carRepository.findAll(pageable)
                    .map(CarDTO::getDto);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        minioService.uploadFile(file, fileName);
        return minioService.getFileUrl(fileName);
    }

    // Вспомогательные методы
    private Car convertToProduct(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPrice(carDTO.getPrice());
        car.setEngineType(carDTO.getEngineType());
        car.setTransmission(carDTO.getTransmission());
        car.setDriveType(carDTO.getDriveType());
        car.setCapacity(carDTO.getCapacity());
        car.setBodyType(carDTO.getBodyType());
        car.setYear(carDTO.getYear());
        car.setEngineVolume(carDTO.getEngineVolume());
        car.setMileage(carDTO.getMileage());
        car.setLocation(carDTO.getLocation());
        car.setTitle(carDTO.getTitle());
        car.setExclusives(carDTO.getExclusives());
        car.setRecommends(carDTO.getRecommends());
        car.setCreateAt(carDTO.getCreateAt());

        convertImages(carDTO, car);

        convertParameters(carDTO, car);

        return car;
    }

    private void convertImages(CarDTO carDTO, Car car) {
        if (carDTO.getImageUrl() != null) {
            List<CarImage> images = new ArrayList<>();
            carDTO.getImageUrl().forEach((type, urls) ->
                    urls.forEach(url ->
                            images.add(new CarImage(null, type, url))
                    ));
            car.setImageUrl(images);
        }
    }

    private void convertParameters(CarDTO carDTO, Car car){
        if(carDTO.getParameters() != null) {
            List<CarParameter> parameters = carDTO.getParameters()
                    .stream()
                    .map(param -> {
                        CarParameter carParameter = new CarParameter();

                        carParameter.setImage(param.getImage());
                        carParameter.setTitle(param.getTitle());
                        carParameter.setAvailable(param.isAvailable());
                        return carParameter;
                    }).collect(Collectors.toList());
            car.setParameters(parameters);

        }
    }

    private void updateProduct(Long id, CarDTO carDTO) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
        updateCarFromDTO(existingCar, carDTO);
        carRepository.save(existingCar);
    }

    private void updateCarFromDTO(Car car, CarDTO carDTO) {
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPrice(carDTO.getPrice());
        car.setEngineType(carDTO.getEngineType());
        car.setTransmission(carDTO.getTransmission());
        car.setDriveType(carDTO.getDriveType());
        car.setCapacity(carDTO.getCapacity());
        car.setBodyType(carDTO.getBodyType());
        car.setYear(carDTO.getYear());
        car.setEngineVolume(carDTO.getEngineVolume());
        car.setMileage(carDTO.getMileage());
        car.setLocation(carDTO.getLocation());
        car.setTitle(carDTO.getTitle());
        car.setExclusives(carDTO.getExclusives());
        car.setRecommends(carDTO.getRecommends());
        car.setCreateAt(carDTO.getCreateAt());

        updateImages(car, carDTO);
        updateParameters(car, carDTO);

    }

    private void updateImages(Car car, CarDTO carDTO) {
        Set<CarImage> imagesToRemove = new HashSet<>(car.getImageUrl());

        updateIfHasUpdates(carDTO, car, imagesToRemove);

        car.getImageUrl().removeAll(imagesToRemove);
    }

    private void updateIfHasUpdates(CarDTO carDTO, Car car, Set<CarImage> imagesToRemove){
        if (carDTO.getImageUrl() != null) {
            carDTO.getImageUrl().forEach((type, urls) ->
                    urls.forEach(url -> {
                        boolean exists = car.getImageUrl().stream()
                                .anyMatch(img -> img.getImageType().equals(type) && img.getImageUrl().equals(url));

                        if (!exists) {
                            car.getImageUrl().add(new CarImage(null, type, url));
                        }

                        imagesToRemove.removeIf(img ->
                                img.getImageType().equals(type) && img.getImageUrl().equals(url));
                    })
            );
        }
    }

    private void updateParameters(Car car, CarDTO carDTO) {
        if (carDTO.getParameters() == null) {
            car.getParameters().clear();
            return;
        }

        Map<Long, CarParameterDTO> newParamsMap = carDTO.getParameters().stream()
                .filter(param -> param.getId() != null)
                .collect(Collectors.toMap(CarParameterDTO::getId, Function.identity()));

        setNewParams(car, newParamsMap);

        newParamsMap.values().forEach(param -> {
            CarParameter newCarParam = new CarParameter();
            newCarParam.setImage(param.getImage());
            newCarParam.setTitle(param.getTitle());
            newCarParam.setAvailable(param.isAvailable());
            car.getParameters().add(newCarParam);
        });
    }

    private void setNewParams(Car car, Map<Long, CarParameterDTO> newParamsMap){
        Iterator<CarParameter> iterator = car.getParameters().iterator();
        while (iterator.hasNext()) {
            CarParameter existingParam = iterator.next();
            CarParameterDTO newParam = newParamsMap.get(existingParam.getId());

            if (newParam != null) {

                existingParam.setImage(newParam.getImage());
                existingParam.setTitle(newParam.getTitle());
                existingParam.setAvailable(newParam.isAvailable());
                newParamsMap.remove(existingParam.getId());
            } else {
                iterator.remove();
            }
        }
    }

    private List<String> uploadImages(MultipartFile[] files) throws IOException {
        if (files == null) return Collections.emptyList();

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadImage(file));
            }
        }
        return urls;
    }

    private void addImagesToMap(Map<String, List<String>> map, String type,
                                MultipartFile[] files) throws IOException {
        if (files != null && files.length > 0) {
            List<String> urls = map.getOrDefault(type, new ArrayList<>());
            urls.addAll(uploadImages(files));
            map.put(type, urls);
        }
    }

    @Override
    public List<OrderDTO> getNewOrders(String search){
        if(search == null || search.trim().isEmpty()){
            return orderRepository.findAllCreated()
                    .stream()
                    .map(OrderDTO::toDTO)
                    .collect(Collectors.toList());
        }

        return orderRepository.findCreatedOrdersBySearch(search)
                .stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getReadyOrders(String search) {
        if(search == null || search.trim().isEmpty()){
            return orderRepository.findAllByStatus()
                    .stream()
                    .map(OrderDTO::toDTO).collect(Collectors.toList());
        }

        return orderRepository.findAllByStatusAndSearch(search.trim())
                .stream()
                .map(OrderDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getNotReadyOrders(String search) {
        if(search == null || search.trim().isEmpty()){
            return orderRepository.findCancelledAndReceivedOrders()
                    .stream().map(OrderDTO::toDTO)
                    .collect(Collectors.toList());
        }

        return orderRepository.findCancelledAndReceivedOrdersBySearch(search.trim())
                .stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());
    }

    private CarDTO buildCarDTOFromJson(
            String title, String brand, String model, String price, String engineType,
            String transmission, String driveType, String bodyType, String year,
            String engineVolume,
            String capacity, String mileage, String exclusives, String recommends,
            String location, String parametersJson
    ) throws JsonProcessingException {
        CarDTO carDTO = new CarDTO();
        carDTO.setTitle(title);
        carDTO.setBrand(brand);
        carDTO.setModel(model);
        carDTO.setPrice(new BigDecimal(price));
        carDTO.setEngineType(engineType);
        carDTO.setTransmission(transmission);
        carDTO.setDriveType(driveType);
        carDTO.setBodyType(bodyType);
        carDTO.setYear(Integer.parseInt(year));
        carDTO.setEngineVolume(Double.parseDouble(engineVolume));
        carDTO.setCapacity(Short.parseShort(capacity));
        carDTO.setMileage(Integer.parseInt(mileage));
        carDTO.setLocation(location);
        carDTO.setExclusives(exclusives);
        carDTO.setRecommends(recommends);

        List<CarParameterDTO> parameters = objectMapper.readValue(
                parametersJson,
                new TypeReference<List<CarParameterDTO>>() {}
        );
        carDTO.setParameters(parameters);

        return carDTO;
    }

    public ResponseEntity<Map<String, Object>> getListingOptions() {
        Map<String, Object> options = new HashMap<>();

        options.put("transmission", Arrays.asList(
                createOption("Механика", "Механика"),
                createOption("Автомат", "Автомат"),
                createOption("Робот", "Робот"),
                createOption("Вариатор", "Вариатор")
        ));

        options.put("engineType", Arrays.asList(
                createOption("Бензин", "Бензин"),
                createOption("Дизель", "Дизель"),
                createOption("Электро", "Электро"),
                createOption("Гибрид", "Гибрид"),
                createOption("ГБО", "ГБО")
        ));

        options.put("driveType", Arrays.asList(
                createOption("Передний", "Передний"),
                createOption("Задний", "Задний"),
                createOption("Полный", "Полный")
        ));

        options.put("capacity", Arrays.asList(
                createOption("2 места", 2),
                createOption("4 места", 4),
                createOption("5 мест", 5),
                createOption("7 мест", 7),
                createOption("9 мест", 9),
                createOption("11 мест", 11)
        ));

        options.put("bodyType", Arrays.asList(
                createOption("Седан", "Седан"),
                createOption("Купе", "Купе"),
                createOption("Кроссовер", "Кроссовер"),
                createOption("Внедорожник", "Внедорожник"),
                createOption("Хэтчбек", "Хэтчбек"),
                createOption("Лифтбек", "Лифтбек"),
                createOption("Универсал", "Универсал"),
                createOption("Фастбэк", "Фастбэк"),
                createOption("Пикап", "Пикап"),
                createOption("Минивэн", "Минивэн"),
                createOption("Кабриолет", "Кабриолет"),
                createOption("Лимузин", "Лимузин")
        ));

        List<String> svgIcons = Arrays.asList(
                "sits", "vent", "hot", "navigation", "key",
                "cond", "camera", "parking", "led", "roof"
        );

        List<Map<String, Object>> parameters = new ArrayList<>();

       addParameters(parameters, svgIcons);

        options.put("parameters", parameters);

        return ResponseEntity.ok(options);
    }

    private void addParameters(List<Map<String, Object>> parameters, List<String> svgIcons){
        for (int i = 0; i < svgIcons.size(); i++) {
            Map<String, Object> param = new HashMap<>();
            param.put("id", i + 1);
            param.put("image", svgIcons.get(i));
            param.put("title", getParameterTitle(svgIcons.get(i)));
            param.put("available", false);
            parameters.add(param);
        }
    }

    private String getParameterTitle(String svgName) {
        return switch (svgName) {
            case "roof" -> "Люк на крыше";
            case "led" -> "Фара(LED)";
            case "parking" -> "Датчик Парковки";
            case "camera" -> "Задняя камера";
            case "cond" -> "Автоматический Кондиционер";
            case "key" -> "Смарт Ключ";
            case "navigation" -> "Навигация";
            case "hot" -> "Подогрев Сидений";
            case "vent" -> "Вентиляционный лист";
            case "sits" -> "Кожаные сидения";
            default -> "Параметр " + svgName;
        };
    }

    private Map<String, Object> createOption(String label, Object value) {
        Map<String, Object> option = new HashMap<>();
        option.put("label", label);
        option.put("value", value);
        return option;
    }
}