package ru.practicum.shareit;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		/*TypeMap<UserDto, User> propertyMapper = modelMapper.createTypeMap(UserDto.class, User.class);
		propertyMapper.addMappings(mapper -> mapper.when(Conditions.isNull()).skip(UserDto::getEmail, User::setEmail));
		propertyMapper.addMappings(mapper -> mapper.when(Conditions.isNull()).skip(UserDto::getName, User::setName));
		*/return modelMapper;
	}

}
