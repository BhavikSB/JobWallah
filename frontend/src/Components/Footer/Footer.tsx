import { IconAnchor, IconBrandFacebook, IconBrandInstagram, IconBrandLinkedin, IconBrandTelegram, IconBrandTwitter, IconBrandWhatsapp, IconBrandX, IconBrandYoutube } from "@tabler/icons-react";
import { IconBriefcase } from "@tabler/icons-react";
import { footerLinks } from "../../Data/Data";
import { useLocation } from "react-router-dom";
import { Divider } from "@mantine/core";

const Footer = () => {
    const location=useLocation();
    return location.pathname!='/signup' && location.pathname!='/login'?<div className="flex flex-col gap-2"><div className="pt-20 pb-5 bg-mine-shaft-950 p-4  flex gap-8 justify-around flex-wrap">
        <div data-aos="fade-up"  data-aos-offset="0"  className="w-1/4 sm-mx:w-1/3   xs-mx:w-1/2 xsm-mx:w-full flex flex-col gap-4">
            <div className="flex gap-1 items-center text-bright-sun-400">
                <img src="/jw0.png" alt="Jobwallah Logo" className="h-12 w-12" />
                <div className="text-xl font-semibold">JobWallah</div>
            </div>
            <div className="text-sm text-mine-shaft-300">JobWallah is a premier career platform connecting talented individuals with top companies. We streamline the job search with comprehensive profiles, skill tracking, and powerful tools for employers.</div>
            <div className="flex gap-3 text-bright-sun-400 [&>a]:bg-mine-shaft-900 [&>a]:p-2 [&>a]:rounded-full [&>a]:cursor-pointer hover:[&>a]:bg-mine-shaft-700">
                <a href="#"><IconBrandInstagram /></a>
                <a href="#"><IconBrandTelegram /></a>
                <a href="#"><IconBrandWhatsapp /></a>
                <a href="#"><IconBrandYoutube /></a>
                <a href="#"><IconBrandLinkedin /></a>
                <a href="#"><IconBrandTwitter /></a>
            </div>
        </div>
        {
            footerLinks.map((item, index) => <div  data-aos-offset="0"  data-aos="fade-up" key={index}>
                <div className="text-lg font-semibold mb-4 text-bright-sun-400">{item.title}</div>
                {
                    item.links.map((link, index) => <div key={index} className="text-mine-shaft-300 text-sm hover:text-bright-sun-400 cursor-pointer mb-1 hover:translate-x-2 transition duration-300 ease-in-out">{link}</div>)
                }
            </div>)
        }
    </div>
    <Divider/>
    <div data-aos="flip-left"  data-aos-offset="0" className="font-medium text-center p-5">
        A JobWallah Creation with 💙 by <a className="text-bright-sun-400 hover:underline font-semibold " href="#">Bhavik, Sarthak, Dhruvi</a>
    </div>
    </div>:<></>
}
export default Footer;