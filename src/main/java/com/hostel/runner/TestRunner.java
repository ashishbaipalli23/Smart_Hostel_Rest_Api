package com.hostel.runner;

import com.hostel.models.Bed;
import com.hostel.repository.BedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import java.util.Optional;

//@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {


    private  final BedRepository bedRepository;


    @Override
    public void run(String... args) throws Exception {


        Optional<Bed> hydBoys01 = bedRepository.findBed("HYD_BOYS_01", "A101-B1");
        if(hydBoys01.isPresent()){
            Bed bed = hydBoys01.get();
            System.out.println(bed.getStatus()+" "+bed.getId());
        }
        else {
            System.out.println("bed not found");
        }

    }
}
