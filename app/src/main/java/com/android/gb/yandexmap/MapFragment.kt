package com.android.gb.yandexmap

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.gb.util.requestLocationPermission
import com.android.gb.yandexmap.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment(), UserLocationObjectListener, CameraListener {

    private val MAPKIT_API_KEY = "3655ebd3-6c47-4d75-8e99-f554672a0915"
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var userLocationLayer: UserLocationLayer? = null
    private var followUserLocation = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        requestLocationPermission(requireContext(), requireActivity())
        onMapReady()
        binding.userLocationFab.setOnClickListener {
            cameraUserPosition()
            followUserLocation = true
        }

        return binding.root
    }


    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.map.mapWindow)
        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(this)
        binding.map.map.addCameraListener(this)
        cameraUserPosition()
    }

    private fun cameraUserPosition() {
        if (userLocationLayer?.cameraPosition() != null) {
            val routeStartLocation = userLocationLayer?.cameraPosition()?.target
            binding.map.map.move(
                CameraPosition(routeStartLocation!!, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            binding.map.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        binding.map.onStart()
        super.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        binding.map.onStop()
        super.onStop()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()

        userLocationView.pin.setIcon(
            ImageProvider.fromResource(
                requireContext(),
                R.drawable.user_arrow
            )
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireContext(),
                R.drawable.user_arrow
            )
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    private fun setAnchor() {
        userLocationLayer?.setAnchor(
            PointF((binding.map.width * 0.5).toFloat(), (binding.map.height * 0.5).toFloat()),
            PointF((binding.map.width * 0.5).toFloat(), (binding.map.height * 0.83).toFloat())
        )

        binding.userLocationFab.setImageResource(R.drawable.ic_my_location_black_24dp)

        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer?.resetAnchor()

        binding.userLocationFab.setImageResource(R.drawable.ic_location_searching_black_24dp)
    }


    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {
        if (p3) {
            if (followUserLocation) {
                setAnchor()
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }
}