package ru.practicum.shareit.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<BookingRequestDto, Booking> propertyMapper =
                modelMapper.createTypeMap(BookingRequestDto.class, Booking.class);
        propertyMapper.addMappings(mapper -> mapper.skip(Booking::setId));
		/*TypeMap<UserDto, User> propertyMapper = modelMapper.createTypeMap(UserDto.class, User.class);
		propertyMapper.addMappings(mapper -> mapper.when(Conditions.isNull()).skip(UserDto::getEmail, User::setEmail));
		propertyMapper.addMappings(mapper -> mapper.when(Conditions.isNull()).skip(UserDto::getName, User::setName));
		*/return modelMapper;
    }
}
